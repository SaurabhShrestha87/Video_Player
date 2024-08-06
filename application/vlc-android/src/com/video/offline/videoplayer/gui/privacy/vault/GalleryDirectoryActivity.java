package com.video.offline.videoplayer.gui.privacy.vault;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.icu.text.DecimalFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.video.offline.videoplayer.R;
import com.video.offline.videoplayer.databinding.ActivityGalleryDirectoryBinding;
import com.video.offline.videoplayer.gui.privacy.lock.LockStore;
import com.video.offline.videoplayer.gui.privacy.vault.adapters.GalleryGridAdapter;
import com.video.offline.videoplayer.gui.privacy.vault.adapters.GalleryPagerAdapter;
import com.video.offline.videoplayer.gui.privacy.vault.data.FileType;
import com.video.offline.videoplayer.gui.privacy.vault.data.GalleryFile;
import com.video.offline.videoplayer.gui.privacy.vault.encryption.Encryption;
import com.video.offline.videoplayer.gui.privacy.vault.encryption.Password;
import com.video.offline.videoplayer.gui.privacy.vault.exception.InvalidPasswordException;
import com.video.offline.videoplayer.gui.privacy.vault.interfaces.IOnDirectoryAdded;
import com.video.offline.videoplayer.gui.privacy.vault.interfaces.IOnProgress;
import com.video.offline.videoplayer.gui.privacy.vault.utils.Dialogs;
import com.video.offline.videoplayer.gui.privacy.vault.utils.FileStuff;
import com.video.offline.videoplayer.gui.privacy.vault.utils.Settings;
import com.video.offline.videoplayer.gui.privacy.vault.utils.StringStuff;
import com.video.offline.videoplayer.gui.privacy.vault.utils.Toaster;
import com.video.offline.videoplayer.gui.privacy.vault.viewmodel.GalleryDirectoryViewModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class GalleryDirectoryActivity extends BaseActivity {
    public static final String EXTRA_DIRECTORY = "d";
    public static final String EXTRA_IS_ALL = "a";
    private static final String TAG = "GalleryDirectoryActivity";
    private static final Object LOCK = new Object();
    private static final int MIN_FILES_FOR_FAST_SCROLL = 60;
    private LockStore lockStore;
    private ActivityGalleryDirectoryBinding binding;
    private GalleryDirectoryViewModel directoryViewModel;

    private GalleryGridAdapter galleryGridAdapter;
    private GalleryPagerAdapter galleryPagerAdapter;
    private Settings settings;
    private Uri currentDirectory;
    private DocumentFile currentDocumentDirectory;
    private boolean inSelectionMode = false;
    private boolean isExporting = false;
    private boolean isCancelled = false;

    private final int foundFiles = 0;
    private final int foundFolders = 0;

    private boolean cancelTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityGalleryDirectoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        lockStore = LockStore.getInstance(this);
        Bundle extras = getIntent().getExtras();
        currentDirectory = null;
        currentDocumentDirectory = null;
        if (extras != null) {
            String dir = extras.getString(EXTRA_DIRECTORY);
            if (dir != null) {
                currentDirectory = Uri.parse(dir);
                currentDocumentDirectory = DocumentFile.fromTreeUri(this, currentDirectory);
            }
        }
        if (currentDirectory == null) {
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.gallery_title));
        }

        directoryViewModel = new ViewModelProvider(this).get(GalleryDirectoryViewModel.class);

        init();
    }

    private void setLoading(boolean loading) {
        binding.cLLoading.findViewById(R.id.cLLoading).setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.cLLoading.findViewById(R.id.txt_progress).setVisibility(View.GONE);
    }

    private void setLoadingProgress(int progress, int total, String doneMB, String totalMB, int percentageDone) {
        binding.cLLoading.findViewById(R.id.cLLoading).setVisibility(View.VISIBLE);
        if (total > 0) {
            ((TextView) binding.cLLoading.findViewById(R.id.txt_progress)).setText(getString(R.string.gallery_importing_progress, progress, total, doneMB, totalMB, percentageDone));
            binding.cLLoading.findViewById(R.id.txt_progress).setVisibility(View.VISIBLE);
        } else {
            binding.cLLoading.findViewById(R.id.txt_progress).setVisibility(View.GONE);
        }
    }

    private void setLoadingWithProgress(int progress, int failed, int total, int stringId) {
        binding.cLLoading.findViewById(R.id.cLLoading).setVisibility(View.VISIBLE);
        if (total > 0) {
            ((TextView) binding.cLLoading.findViewById(R.id.txt_progress)).setText(getString(stringId, progress, total, failed));
            binding.cLLoading.findViewById(R.id.txt_progress).setVisibility(View.VISIBLE);
        } else {
            binding.cLLoading.findViewById(R.id.txt_progress).setVisibility(View.GONE);
        }
    }

    private void setLoadingAllWithProgress() {
        binding.cLLoading.findViewById(R.id.cLLoading).setVisibility(View.VISIBLE);
        ((TextView) binding.cLLoading.findViewById(R.id.txt_progress)).setText(getString(R.string.gallery_loading_all_progress, foundFiles, foundFolders));
        binding.cLLoading.findViewById(R.id.txt_progress).setVisibility(View.VISIBLE);
    }

    private void init() {
        settings = Settings.getInstance(this);
        if (settings.isLocked()) {
            finish();
            return;
        }
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, currentDirectory);
        if ((documentFile != null && documentFile.isDirectory() && documentFile.exists())) {
            setupViewpager();
            setupRecycler();
            setupObservers();
            setClickListeners();
            if (!directoryViewModel.isInitialised()) {
                findFilesIn(currentDirectory);
            }
        } else {
            Toaster.getInstance(this).showLong(getString(R.string.directory_does_not_exist));
            finish();
        }
    }

    private void setupObservers() {

    }

    private void setupFileCount() {
        int videosCount = 0;
        int imagesCount = 0;
        long totalSize = 0L;
        for (GalleryFile galleryFile : directoryViewModel.getGalleryFiles()) {
            if (galleryFile.isVideo()) {
                videosCount++;
            } else if (galleryFile.getFileType() == FileType.GIF || galleryFile.getFileType() == FileType.IMAGE) {
                imagesCount++;
            }
            totalSize = totalSize + galleryFile.getSize();
        }
        binding.videoCountTextView.setText(MessageFormat.format("{0} Videos | {1} Image/Gif | {2}", videosCount, imagesCount, StringStuff.bytesToReadableString(totalSize)));
    }

    private void setClickListeners() {
        binding.btnDeleteFiles.setOnClickListener(v -> Dialogs.showConfirmationDialog(this, getString(R.string.dialog_delete_files_title), getResources().getQuantityString(R.plurals.dialog_delete_files_message, galleryGridAdapter.getSelectedFiles().size()), (dialog, which) -> deleteSelectedFiles()));
        binding.btnImportFiles.setOnClickListener(v -> showImportOverlay(true));
        binding.btnImportImages.setOnClickListener(v -> {
            FileStuff.pickImageFiles(activityLauncher, result -> onImportImagesOrVideos(result.getData()));
            showImportOverlay(false);
        });
        binding.btnImportVideos.setOnClickListener(v -> {
            FileStuff.pickVideoFiles(activityLauncher, result -> onImportImagesOrVideos(result.getData()));
            showImportOverlay(false);
        });
        binding.importChooseOverlay.setOnClickListener(v -> showImportOverlay(false));
        binding.sortIv.setOnClickListener(v -> sortView());
        binding.viewModeIv.setOnClickListener(v -> changeViewMode());
    }

    private void sortView() {
        Toast.makeText(this, "TODO: sort view!", Toast.LENGTH_SHORT).show();
    }

    private void changeViewMode() {
        Toast.makeText(this, "TODO: Change view MODE!", Toast.LENGTH_SHORT).show();
    }

    private void onImportImagesOrVideos(@Nullable Intent data) {
        if (data != null) {
            List<DocumentFile> documentFiles = FileStuff.getDocumentsFromDirectoryResult(this, data);
            if (!documentFiles.isEmpty()) {
                importFiles(documentFiles);
            }
        }
    }

    private void importFiles(List<DocumentFile> documentFiles) {
        Dialogs.doNotShowImportGalleryChooseDestinationDialog(this, settings, documentFiles.size(), new Dialogs.IOnDirectorySelected() {
            @Override
            public void onDirectorySelected(@NonNull DocumentFile directory, boolean deleteOriginal) {
                importToDirectory(documentFiles, directory, deleteOriginal);
            }

            @Override
            public void onOtherDirectory() {
                Toast.makeText(GalleryDirectoryActivity.this, "CANNOT CREATE ADDITIONAL DIRECTORY!", Toast.LENGTH_SHORT).show();
//                viewModel.setFilesToAdd(documentFiles);
//                binding.btnAddFolder.performClick();
            }
        });
    }

    private void importToDirectory(@NonNull List<DocumentFile> documentFiles, @NonNull DocumentFile directory, boolean deleteOriginal) {
        new Thread(() -> {
            double totalBytes = 0;
            for (DocumentFile file : documentFiles) {
                totalBytes += file.length();
            }
            final DecimalFormat decimalFormat = new DecimalFormat("0.00");
            final String totalMB = decimalFormat.format(totalBytes / 1000000.0);
            final int[] progress = new int[]{1};
            final double[] bytesDone = new double[]{0};
            final long[] lastPublish = {0};
            double finalTotalSize = totalBytes;
            final IOnProgress onProgress = progress1 -> {
                if (System.currentTimeMillis() - lastPublish[0] > 20) {
                    lastPublish[0] = System.currentTimeMillis();
                    runOnUiThread(() -> setLoadingProgress(progress[0], documentFiles.size(), decimalFormat.format((bytesDone[0] + progress1) / 1000000.0), totalMB, (int) Math.round((bytesDone[0] + progress1) / finalTotalSize * 100.0)));
                }
            };
            for (DocumentFile file : documentFiles) {
                if (cancelTask) {
                    cancelTask = false;
                    break;
                }
                Pair<Boolean, Boolean> imported = new Pair<>(false, false);
                try {
                    imported = Encryption.importFileToDirectory(this, file, directory, settings, onProgress);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                progress[0]++;
                bytesDone[0] += file.length();
                if (!imported.first) {
                    progress[0]--;
                    runOnUiThread(() -> Toaster.getInstance(this).showLong(getString(R.string.gallery_importing_error, file.getName())));
                } else if (!imported.second) {
                    runOnUiThread(() -> Toaster.getInstance(this).showLong(getString(R.string.gallery_importing_error_no_thumb, file.getName())));
                }
                if (deleteOriginal && imported.first) {
                    file.delete();
                }
            }
            runOnUiThread(() -> {
                Toaster.getInstance(GalleryDirectoryActivity.this).showLong(getString(R.string.gallery_importing_done, progress[0] - 1));
                refreshFiles();
                setLoading(false);
            });
        }).start();
    }

    public void onSelectionChanged(int selected) {
        binding.btnDeleteFiles.setText(getString(R.string.gallery_delete_selected_files, selected));
    }

    private void deleteSelectedFiles() {
        setLoading(true);
        new Thread(() -> {
            synchronized (LOCK) {
                List<GalleryFile> selectedFiles = galleryGridAdapter.getSelectedFiles();
                ConcurrentLinkedQueue<GalleryFile> queue = new ConcurrentLinkedQueue<>(selectedFiles);
                List<Integer> positionsDeleted = Collections.synchronizedList(new ArrayList<>());
                AtomicInteger deletedCount = new AtomicInteger(0);
                AtomicInteger failedCount = new AtomicInteger(0);
                final int total = selectedFiles.size();
                final int threadCount;
                if (total < 4) {
                    threadCount = 1;
                } else if (total < 20) {
                    threadCount = 4;
                } else {
                    threadCount = 8;
                }
                runOnUiThread(() -> setLoadingWithProgress(0, 0, 0, R.string.gallery_deleting_progress));
                List<Thread> threads = new ArrayList<>(threadCount);
                for (int i = 0; i < threadCount; i++) {
                    Thread t = new Thread(() -> {
                        GalleryFile f;
                        while (!isCancelled && (f = queue.poll()) != null) {
                            deleteFile(total, f, positionsDeleted, deletedCount, failedCount);
                        }
                    });
                    threads.add(t);
                    t.start();
                }

                for (Thread thread : threads) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isCancelled) {
                    isCancelled = false;
                }
                Collections.sort(positionsDeleted);
                runOnUiThread(() -> {
                    while (!positionsDeleted.isEmpty()) {
                        int pos = positionsDeleted.remove(positionsDeleted.size() - 1);
                        directoryViewModel.getGalleryFiles().remove(pos);
                        galleryGridAdapter.notifyItemRemoved(pos);
                        galleryPagerAdapter.notifyItemRemoved(pos);
                        showHideRecyclerView();
                        setupFileCount();
                    }
                    galleryGridAdapter.onSelectionModeChanged(false);
                    setLoading(false);
                });
            }
        }).start();
    }

    private void deleteFile(int total, GalleryFile file, List<Integer> positionsDeleted, AtomicInteger deletedCount, AtomicInteger failedCount) {
        if (file == null || isCancelled) {
            return;
        }
        boolean deleted = FileStuff.deleteFile(this, file.getUri());
        FileStuff.deleteFile(this, file.getThumbUri());
        FileStuff.deleteFile(this, file.getNoteUri());
        if (deleted) {
            deletedCount.addAndGet(1);
            int i = directoryViewModel.getGalleryFiles().indexOf(file);
            if (i >= 0) {
                positionsDeleted.add(i);
            }
        } else {
            failedCount.addAndGet(1);
        }
        runOnUiThread(() -> setLoadingWithProgress(deletedCount.get() + failedCount.get(), failedCount.get(), total, R.string.gallery_deleting_progress));
    }

    private void setupRecycler() {
        if (directoryViewModel.isInitialised()) {
            binding.recyclerView.setFastScrollEnabled(directoryViewModel.getGalleryFiles().size() > MIN_FILES_FOR_FAST_SCROLL);
        } else {
            binding.recyclerView.setFastScrollEnabled(false);
        }
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 6 : 3;
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        galleryGridAdapter = new GalleryGridAdapter(this, directoryViewModel.getGalleryFiles(), settings.showFilenames());
        galleryGridAdapter.setOnFileDeleted(pos -> galleryPagerAdapter.notifyItemRemoved(pos));
        binding.recyclerView.setAdapter(galleryGridAdapter);
        galleryGridAdapter.setOnFileCLicked(pos -> showViewpager(true, pos, true));
        galleryGridAdapter.setOnSelectionModeChanged(this::onSelectionModeChanged);
        galleryGridAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.e(TAG, "onChanged: adapter size : " + galleryGridAdapter.getItemCount());
                Log.e(TAG, "onChanged: galleryFiles size : " + directoryViewModel.getGalleryFiles());
            }
        });
    }

    private void onSelectionModeChanged(boolean inSelectionMode) {
        this.inSelectionMode = inSelectionMode;
        if (inSelectionMode) {
            binding.lLSelectionButtons.setVisibility(View.VISIBLE);
        } else {
            binding.lLSelectionButtons.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    private void setupViewpager() {
        galleryPagerAdapter = new GalleryPagerAdapter(this, directoryViewModel.getGalleryFiles(), pos -> galleryGridAdapter.notifyItemRemoved(pos), currentDocumentDirectory);
        binding.viewPager.setAdapter(galleryPagerAdapter);
        //Log.e(TAG, "setupViewpager: " + viewModel.getCurrentPosition() + " " + viewModel.isFullscreen());
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.recyclerView.scrollToPosition(position);
                directoryViewModel.setCurrentPosition(position);
            }
        });
        binding.viewPager.postDelayed(() -> {
            binding.viewPager.setCurrentItem(directoryViewModel.getCurrentPosition(), false);
            showViewpager(directoryViewModel.isViewpagerVisible(), directoryViewModel.getCurrentPosition(), false);
        }, 200);
    }

    private void showViewpager(boolean show, int pos, boolean animate) {
        //Log.e(TAG, "showViewpager: " + show + " " + pos);
        directoryViewModel.setViewpagerVisible(show);
        galleryPagerAdapter.showPager(show);
        if (show) {
            binding.viewPager.setVisibility(View.VISIBLE);
            binding.viewPager.setCurrentItem(pos, false);
        } else {
            binding.viewPager.setVisibility(View.GONE);
            if (pos >= 0) {
                RecyclerView.ViewHolder viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(pos);
                if (viewHolder != null && animate) {
                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(500);
                    animation.setInterpolator(new LinearInterpolator());
                    viewHolder.itemView.startAnimation(animation);
                }
                binding.recyclerView.scrollToPosition(pos);
            }
        }
    }

    private void refreshFiles() {
        Uri directoryUri = currentDirectory;
        setLoading(true);
        new Thread(() -> {
            List<GalleryFile> oldGalleryFiles = directoryViewModel.getGalleryFiles();
            List<GalleryFile> galleryFiles = FileStuff.getFilesInFolder(this, directoryUri);
            if (oldGalleryFiles.size() == galleryFiles.size()) {
                return;
            }
            runOnUiThread(() -> {
                setLoading(false);
                if (galleryFiles.size() > MIN_FILES_FOR_FAST_SCROLL) {
                    binding.recyclerView.setFastScrollEnabled(true);
                }
                synchronized (LOCK) {
                    directoryViewModel.resetInitialised(galleryFiles);
                    galleryGridAdapter.notifyDataSetChanged();
                    galleryPagerAdapter.notifyDataSetChanged();
                    showHideRecyclerView();
                    setupFileCount();
                }
            });
        }).start();
    }

    private void findFilesIn(Uri directoryUri) {
        setLoading(true);
        new Thread(() -> {
            List<GalleryFile> galleryFiles = FileStuff.getFilesInFolder(this, directoryUri);

            runOnUiThread(() -> {
                setLoading(false);
                if (galleryFiles.size() > MIN_FILES_FOR_FAST_SCROLL) {
                    binding.recyclerView.setFastScrollEnabled(true);
                }
                synchronized (LOCK) {
                    if (directoryViewModel.isInitialised()) {
                        return;
                    }
                    directoryViewModel.setInitialised(galleryFiles);
                    showHideRecyclerView();
                    setupFileCount();
                    galleryGridAdapter.notifyItemRangeInserted(0, galleryFiles.size());
                    galleryPagerAdapter.notifyItemRangeInserted(0, galleryFiles.size());
                }
            });

            for (int i = 0; i < galleryFiles.size(); i++) {
                GalleryFile g = galleryFiles.get(i);
                if (g.isDirectory()) {
                    int finalI = i;
                    new Thread(() -> {
                        List<GalleryFile> found = FileStuff.getFilesInFolder(this, g.getUri());
                        if (found.isEmpty()) {
                            runOnUiThread(() -> {
                                synchronized (LOCK) {
                                    int i1 = directoryViewModel.getGalleryFiles().indexOf(g);
                                    if (i1 >= 0) {
                                        directoryViewModel.getGalleryFiles().remove(i1);
                                        galleryGridAdapter.notifyItemRemoved(i1);
                                        galleryPagerAdapter.notifyItemRemoved(i1);
                                    }
                                }
                            });
                        } else {
                            g.setFilesInDirectory(found);
                            runOnUiThread(() -> galleryGridAdapter.notifyItemChanged(finalI));
                        }
                    }).start();
                }
            }
        }).start();
    }

    private void showHideRecyclerView() {
        Log.e(TAG, "showHideRecyclerView: size : " + directoryViewModel.getGalleryFiles().size());
        if (!directoryViewModel.getGalleryFiles().isEmpty()) {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.noDataView.setVisibility(View.GONE);
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.noDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        if (galleryPagerAdapter != null) {
            galleryPagerAdapter.pausePlayers();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (galleryPagerAdapter != null) {
            galleryPagerAdapter.releasePlayers();
        }
        super.onDestroy();
    }

    private void lock() {
        Password.lock(this, settings);
        finishAffinity();
        startActivity(new Intent(this, LaunchActivity.class));
    }

    private void exportSelected() {
        Dialogs.showConfirmationDialog(this, getString(R.string.dialog_export_selected_title), getString(R.string.dialog_export_selected_message, FileStuff.getFilenameWithPathFromUri(currentDirectory)), (dialog, which) -> {
            isExporting = true;
            final List<GalleryFile> galleryFilesCopy = new ArrayList<>(galleryGridAdapter.getSelectedFiles());
            setLoadingWithProgress(0, 0, galleryFilesCopy.size(), R.string.gallery_exporting_progress);
            galleryGridAdapter.onSelectionModeChanged(false);
            new Thread(() -> {
                final int[] exported = {0};
                final int[] failed = {0};
                for (GalleryFile f : galleryFilesCopy) {
                    if (isFinishing() || isDestroyed() || !isExporting) {
                        break;
                    }
                    Encryption.IOnUriResult result = new Encryption.IOnUriResult() {
                        @Override
                        public void onUriResult(Uri outputUri) {
                            exported[0]++;
                        }

                        @Override
                        public void onError(Exception e) {
                            failed[0]++;
                        }

                        @Override
                        public void onInvalidPassword(InvalidPasswordException e) {
                            failed[0]++;
                        }
                    };
                    Encryption.decryptAndExport(this, f.getUri(), currentDocumentDirectory, f, settings.getTempPassword(), result, f.isVideo());
                    runOnUiThread(() -> setLoadingWithProgress(exported[0], failed[0], galleryFilesCopy.size(), R.string.gallery_exporting_progress));
                }
                runOnUiThread(() -> {
                    isExporting = false;
                    setLoading(false);
                    if (failed[0] == 0) {
                        Toaster.getInstance(this).showLong(getString(R.string.gallery_selected_files_exported, exported[0]));
                    } else {
                        Toaster.getInstance(this).showLong(getString(R.string.gallery_selected_files_exported_with_failed, exported[0], failed[0]));
                    }
                });
            }).start();
        });
    }

    private void copySelected() {
        final List<GalleryFile> galleryFilesCopy = new ArrayList<>(galleryGridAdapter.getSelectedFiles());
        isExporting = true;
        Dialogs.showCopyMoveChooseDestinationDialog(this, settings, galleryFilesCopy.size(), new Dialogs.IOnDirectorySelected() {
            @Override
            public void onDirectorySelected(@NonNull DocumentFile directory, boolean deleteOriginal) {
                setLoadingWithProgress(0, 0, galleryFilesCopy.size(), R.string.gallery_copying_progress);
                galleryGridAdapter.onSelectionModeChanged(false);
                new Thread(() -> {
                    final int[] copied = {0};
                    final int[] failed = {0};
                    for (GalleryFile f : galleryFilesCopy) {
                        if (isFinishing() || isDestroyed() || !isExporting) {
                            break;
                        }
                        boolean success = FileStuff.copyTo(GalleryDirectoryActivity.this, f, directory);
                        if (success) {
                            copied[0]++;
                        } else {
                            failed[0]++;
                        }
                        runOnUiThread(() -> setLoadingWithProgress(copied[0], failed[0], galleryFilesCopy.size(), R.string.gallery_copying_progress));
                    }
                    runOnUiThread(() -> {
                        isExporting = false;
                        setLoading(false);
                        if (failed[0] == 0) {
                            Toaster.getInstance(GalleryDirectoryActivity.this).showLong(getString(R.string.gallery_selected_files_copied, copied[0]));
                        } else {
                            Toaster.getInstance(GalleryDirectoryActivity.this).showLong(getString(R.string.gallery_selected_files_copied_with_failed, copied[0], failed[0]));
                        }
                    });
                }).start();
            }

            @Override
            public void onOtherDirectory() {
                isExporting = false;
                onCopyMoveDirectoryAdded(new IOnDirectoryAdded() {
                    @Override
                    public void onAddedAsRoot() {
                        copySelected();
                    }

                    @Override
                    public void onAddedAsChildOf(@NonNull Uri parentUri) {
                        copySelected();
                    }

                    @Override
                    public void onAlreadyExists(boolean isRootDir) {
                        copySelected();
                    }
                });
            }
        });
    }

    private void moveSelected() {
        final List<GalleryFile> galleryFilesCopy = new ArrayList<>(galleryGridAdapter.getSelectedFiles());
        isExporting = true;
        Dialogs.showCopyMoveChooseDestinationDialog(this, settings, galleryFilesCopy.size(), new Dialogs.IOnDirectorySelected() {
            @Override
            public void onDirectorySelected(@NonNull DocumentFile directory, boolean deleteOriginal) {
                setLoadingWithProgress(0, 0, galleryFilesCopy.size(), R.string.gallery_copying_progress);
                galleryGridAdapter.onSelectionModeChanged(false);
                new Thread(() -> {
                    final int[] moved = {0};
                    final int[] failed = {0};
                    List<GalleryFile> removed = new ArrayList<>();
                    for (GalleryFile f : galleryFilesCopy) {
                        if (isFinishing() || isDestroyed() || !isExporting) {
                            break;
                        }
                        boolean success = FileStuff.moveTo(GalleryDirectoryActivity.this, f, directory);
                        if (success) {
                            boolean deleted = FileStuff.deleteFile(GalleryDirectoryActivity.this, f.getUri());
                            FileStuff.deleteFile(GalleryDirectoryActivity.this, f.getThumbUri());
                            FileStuff.deleteFile(GalleryDirectoryActivity.this, f.getNoteUri());
                            moved[0]++;
                            removed.add(f);
                        } else {
                            failed[0]++;
                        }
                        runOnUiThread(() -> setLoadingWithProgress(moved[0], failed[0], galleryFilesCopy.size(), R.string.gallery_moving_progress));
                    }
                    runOnUiThread(() -> {
                        isExporting = false;
                        setLoading(false);
                        if (failed[0] == 0) {
                            Toaster.getInstance(GalleryDirectoryActivity.this).showLong(getString(R.string.gallery_selected_files_moved, moved[0]));
                        } else {
                            Toaster.getInstance(GalleryDirectoryActivity.this).showLong(getString(R.string.gallery_selected_files_moved_with_failed, moved[0], failed[0]));
                        }
                        synchronized (LOCK) {
                            for (GalleryFile galleryFile : removed) {
                                int index = directoryViewModel.getGalleryFiles().indexOf(galleryFile);
                                if (index >= 0) {
                                    directoryViewModel.getGalleryFiles().remove(index);
                                    galleryGridAdapter.notifyItemRemoved(index);
                                    galleryPagerAdapter.notifyItemRemoved(index);
                                }
                            }
                        }
                    });
                }).start();
            }

            @Override
            public void onOtherDirectory() {
                isExporting = false;
                onCopyMoveDirectoryAdded(new IOnDirectoryAdded() {
                    @Override
                    public void onAddedAsRoot() {
                        moveSelected();
                    }

                    @Override
                    public void onAddedAsChildOf(@NonNull Uri parentUri) {
                        moveSelected();
                    }

                    @Override
                    public void onAlreadyExists(boolean isRootDir) {
                        moveSelected();
                    }
                });
            }
        });
    }

    private void onCopyMoveDirectoryAdded(IOnDirectoryAdded iOnDirectoryAdded) {
        activityLauncher.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    DocumentFile documentFile = DocumentFile.fromTreeUri(GalleryDirectoryActivity.this, uri);
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    settings.addGalleryDirectory(documentFile.getUri(), iOnDirectoryAdded);
                }
            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                galleryGridAdapter.onSelectionModeChanged(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery_directory, menu);
        menu.findItem(R.id.unlock_with_fingerprint).setChecked(lockStore.isBiometricUnlockEnabled());
        menu.findItem(R.id.toggle_filename).setVisible(!inSelectionMode);
        menu.findItem(R.id.select_all).setVisible(inSelectionMode);
        menu.findItem(R.id.export_selected).setVisible(inSelectionMode);
        return super.onCreateOptionsMenu(menu);
    }

    private void showImportOverlay(boolean show) {
        Log.e(TAG, "showImportOverlay: size : " + directoryViewModel.getGalleryFiles().size());
        binding.cLImportChoose.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.lock) {
            lock();
            return true;
        } else if (id == R.id.toggle_filename) {
            settings.setShowFilenames(galleryGridAdapter.toggleFilenames());
            return true;
        } else if (id == R.id.select_all) {
            galleryGridAdapter.selectAll();
            return true;
        } else if (id == R.id.export_selected) {
            exportSelected();
            return true;
        } else if (id == R.id.copy_selected) {
            copySelected();
            return true;
        } else if (id == R.id.move_selected) {
            moveSelected();
            return true;
        } else if (id == R.id.edit_included_folders) {
            Dialogs.showEditIncludedFolders(this, settings, selectedToRemove -> {
                settings.removeGalleryDirectories(selectedToRemove);
                // TODO
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                Toaster.getInstance(this).showLong(getResources().getQuantityString(R.plurals.edit_included_removed, selectedToRemove.size(), selectedToRemove.size()));
            });
        } else if (id == R.id.reset_password) {
            Intent intent = new Intent(this, LaunchActivity.class);
            intent.putExtra("reset", true);
            startActivity(intent);
            finish();
        } else if (id == R.id.unlock_with_fingerprint) {
            lockStore.setBiometricUnlockEnabled(!lockStore.isBiometricUnlockEnabled());
            item.setChecked(lockStore.isBiometricUnlockEnabled());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (directoryViewModel.isViewpagerVisible()) {
            showViewpager(false, directoryViewModel.getCurrentPosition(), true);
        } else if (isExporting) {
            isExporting = false;
        } else if (binding.cLLoading.findViewById(R.id.cLLoading).getVisibility() == View.VISIBLE) {
            isCancelled = true;
        } else if (inSelectionMode && galleryGridAdapter != null) {
            galleryGridAdapter.onSelectionModeChanged(false);
        } else {
            super.onBackPressed();
        }
    }
}