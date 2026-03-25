<?php
// Simple script to convert large PNG to WebP and update database
$dir = __DIR__ . '/storage/app/public/products';

// Find PNG files
$pngs = glob("$dir/*.png");
echo "Found " . count($pngs) . " PNG files\n";

foreach ($pngs as $pngPath) {
    $pngName = basename($pngPath);
    $pngSize = filesize($pngPath);
    
    // Load PNG
    $img = imagecreatefrompng($pngPath);
    if (!$img) {
        echo "SKIP: Cannot load $pngName\n";
        continue;
    }
    
    $w = imagesx($img);
    $h = imagesy($img);
    
    // Resize to max 800x800
    $max = 800;
    if ($w > $max || $h > $max) {
        $ratio = min($max / $w, $max / $h);
        $nw = (int)($w * $ratio);
        $nh = (int)($h * $ratio);
        $resized = imagecreatetruecolor($nw, $nh);
        imagecopyresampled($resized, $img, 0, 0, 0, 0, $nw, $nh, $w, $h);
        imagedestroy($img);
        $img = $resized;
        echo "Resized: {$w}x{$h} -> {$nw}x{$nh}\n";
    }
    
    // Save as WebP
    $webpName = str_replace('.png', '.webp', $pngName);
    $webpPath = "$dir/$webpName";
    imagewebp($img, $webpPath, 75);
    imagedestroy($img);
    
    clearstatcache(true, $webpPath);
    $webpSize = filesize($webpPath);
    
    $savings = round(($pngSize - $webpSize) / $pngSize * 100);
    echo "$pngName (" . round($pngSize/1024) . " KB) -> $webpName (" . round($webpSize/1024) . " KB) [saved {$savings}%]\n";
    
    // Update database: change image_url from .png to .webp
    try {
        $pdo = new PDO('mysql:host=127.0.0.1;port=3306;dbname=musclecart_db', 'root', '');
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        
        $stmt = $pdo->prepare("UPDATE products SET image_url = ? WHERE image_url = ?");
        $result = $stmt->execute([$webpName, $pngName]);
        $affected = $stmt->rowCount();
        echo "Database updated: $affected rows changed ($pngName -> $webpName)\n";
        
        // Delete old PNG
        if ($affected > 0) {
            unlink($pngPath);
            echo "Deleted old PNG: $pngName\n";
        }
    } catch (Exception $e) {
        echo "DB Error: " . $e->getMessage() . "\n";
    }
}

// Also handle admin storage
$adminDir = __DIR__ . '/../../musclecart-admin/storage/app/public/products';
if (is_dir($adminDir)) {
    echo "\n--- Admin Images ---\n";
    $adminPngs = glob("$adminDir/*.png");
    foreach ($adminPngs as $pngPath) {
        $pngName = basename($pngPath);
        $img = imagecreatefrompng($pngPath);
        if (!$img) continue;
        
        $w = imagesx($img); $h = imagesy($img);
        $max = 800;
        if ($w > $max || $h > $max) {
            $ratio = min($max/$w, $max/$h);
            $nw = (int)($w*$ratio); $nh = (int)($h*$ratio);
            $r = imagecreatetruecolor($nw, $nh);
            imagecopyresampled($r, $img, 0,0,0,0, $nw,$nh, $w,$h);
            imagedestroy($img); $img = $r;
        }
        
        $webpName = str_replace('.png', '.webp', $pngName);
        $webpPath = "$adminDir/$webpName";
        imagewebp($img, $webpPath, 75);
        imagedestroy($img);
        
        clearstatcache(true, $webpPath);
        $oldSize = filesize($pngPath);
        $newSize = filesize($webpPath);
        echo "$pngName (" . round($oldSize/1024) . " KB) -> $webpName (" . round($newSize/1024) . " KB)\n";
        
        unlink($pngPath);
        echo "Deleted: $pngName\n";
    }
}

echo "\nDone!\n";
