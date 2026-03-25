<?php
/**
 * Compress existing product images in storage
 * Converts large PNG images to optimized JPEG and resizes to max 800x800
 */

$storagePath = __DIR__ . '/storage/app/public/products';

if (!is_dir($storagePath)) {
    echo "ERROR: Storage path not found: $storagePath\n";
    exit(1);
}

$files = scandir($storagePath);
$compressed = 0;
$skipped = 0;

foreach ($files as $file) {
    if ($file === '.' || $file === '..') continue;
    
    $filePath = $storagePath . '/' . $file;
    $fileSize = filesize($filePath);
    $ext = strtolower(pathinfo($file, PATHINFO_EXTENSION));
    
    echo "\n--- Processing: $file (Size: " . round($fileSize / 1024) . " KB) ---\n";
    
    // Skip small files (already optimized)
    if ($fileSize < 100 * 1024) { // Less than 100KB
        echo "  SKIP: Already small enough\n";
        $skipped++;
        continue;
    }
    
    // Load image based on type
    $image = null;
    if ($ext === 'png') {
        $image = @imagecreatefrompng($filePath);
    } elseif ($ext === 'jpg' || $ext === 'jpeg') {
        $image = @imagecreatefromjpeg($filePath);
    } elseif ($ext === 'webp') {
        $image = @imagecreatefromwebp($filePath);
    }
    
    if (!$image) {
        echo "  SKIP: Cannot read image\n";
        $skipped++;
        continue;
    }
    
    $origWidth = imagesx($image);
    $origHeight = imagesy($image);
    echo "  Original dimensions: {$origWidth}x{$origHeight}\n";
    
    // Resize if larger than 800px
    $maxDim = 800;
    if ($origWidth > $maxDim || $origHeight > $maxDim) {
        $ratio = min($maxDim / $origWidth, $maxDim / $origHeight);
        $newWidth = (int)($origWidth * $ratio);
        $newHeight = (int)($origHeight * $ratio);
        
        $resized = imagecreatetruecolor($newWidth, $newHeight);
        
        // Preserve transparency for PNG
        if ($ext === 'png') {
            imagealphablending($resized, false);
            imagesavealpha($resized, true);
        }
        
        imagecopyresampled($resized, $image, 0, 0, 0, 0, $newWidth, $newHeight, $origWidth, $origHeight);
        imagedestroy($image);
        $image = $resized;
        echo "  Resized to: {$newWidth}x{$newHeight}\n";
    }
    
    // Save as optimized format
    if ($ext === 'webp') {
        // Re-save webp with quality 75
        imagewebp($image, $filePath, 75);
    } else {
        // Convert PNG/JPG to JPEG with quality 75
        // Save with same filename (overwrite)
        imagejpeg($image, $filePath, 75);
    }
    
    imagedestroy($image);
    
    $newSize = filesize($filePath);
    $savings = round(($fileSize - $newSize) / $fileSize * 100);
    echo "  Compressed: " . round($fileSize / 1024) . " KB -> " . round($newSize / 1024) . " KB (saved {$savings}%)\n";
    $compressed++;
}

echo "\n=== DONE ===\n";
echo "Compressed: $compressed files\n";
echo "Skipped: $skipped files\n";

// Also compress admin storage images if they exist
$adminPath = __DIR__ . '/../../musclecart-admin/storage/app/public/products';
if (is_dir($adminPath)) {
    echo "\n=== Compressing Admin Images ===\n";
    $adminFiles = scandir($adminPath);
    foreach ($adminFiles as $file) {
        if ($file === '.' || $file === '..') continue;
        
        $filePath = $adminPath . '/' . $file;
        $fileSize = filesize($filePath);
        $ext = strtolower(pathinfo($file, PATHINFO_EXTENSION));
        
        if ($fileSize < 100 * 1024) continue; // Skip small files
        
        $image = null;
        if ($ext === 'png') $image = @imagecreatefrompng($filePath);
        elseif ($ext === 'jpg' || $ext === 'jpeg') $image = @imagecreatefromjpeg($filePath);
        elseif ($ext === 'webp') $image = @imagecreatefromwebp($filePath);
        
        if (!$image) continue;
        
        $origWidth = imagesx($image);
        $origHeight = imagesy($image);
        
        $maxDim = 800;
        if ($origWidth > $maxDim || $origHeight > $maxDim) {
            $ratio = min($maxDim / $origWidth, $maxDim / $origHeight);
            $newWidth = (int)($origWidth * $ratio);
            $newHeight = (int)($origHeight * $ratio);
            $resized = imagecreatetruecolor($newWidth, $newHeight);
            if ($ext === 'png') { imagealphablending($resized, false); imagesavealpha($resized, true); }
            imagecopyresampled($resized, $image, 0, 0, 0, 0, $newWidth, $newHeight, $origWidth, $origHeight);
            imagedestroy($image);
            $image = $resized;
        }
        
        if ($ext === 'webp') imagewebp($image, $filePath, 75);
        else imagejpeg($image, $filePath, 75);
        
        imagedestroy($image);
        $newSize = filesize($filePath);
        echo "  $file: " . round($fileSize / 1024) . " KB -> " . round($newSize / 1024) . " KB\n";
    }
}
