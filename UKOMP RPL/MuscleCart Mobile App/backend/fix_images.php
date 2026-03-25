<?php
// Fix corrupted PNG files (actually JPEG data) and convert to WebP
$dir = __DIR__ . '/storage/app/public/products';
$adminDir = __DIR__ . '/../../musclecart-admin/storage/app/public/products';

function fixAndConvert($dir, $updateDb = true) {
    $pngs = glob("$dir/*.png");
    echo "Found " . count($pngs) . " .png files in $dir\n";
    
    foreach ($pngs as $f) {
        $name = basename($f);
        $size = filesize($f);
        $data = file_get_contents($f, false, null, 0, 4);
        $hex = bin2hex($data);
        
        $img = null;
        
        // Detect actual format from magic bytes
        if (substr($hex, 0, 4) === 'ffd8') {
            // It's actually JPEG!
            echo "$name: Actually JPEG ({$size} bytes) - ";
            $img = imagecreatefromjpeg($f);
        } elseif (substr($hex, 0, 8) === '89504e47') {
            echo "$name: Real PNG ({$size} bytes) - ";
            $img = imagecreatefrompng($f);
        } else {
            echo "$name: Unknown format ($hex)\n";
            // Try imagecreatefromstring as fallback
            $img = imagecreatefromstring(file_get_contents($f));
        }
        
        if (!$img) {
            echo "FAILED to load\n";
            continue;
        }
        
        $w = imagesx($img);
        $h = imagesy($img);
        
        // Resize if needed
        $max = 800;
        if ($w > $max || $h > $max) {
            $ratio = min($max / $w, $max / $h);
            $nw = (int)($w * $ratio);
            $nh = (int)($h * $ratio);
            $r = imagecreatetruecolor($nw, $nh);
            imagecopyresampled($r, $img, 0, 0, 0, 0, $nw, $nh, $w, $h);
            imagedestroy($img);
            $img = $r;
            echo "resized {$w}x{$h}->{$nw}x{$nh} - ";
        }
        
        // Save as WebP
        $webpName = str_replace('.png', '.webp', $name);
        $webpPath = "$dir/$webpName";
        imagewebp($img, $webpPath, 75);
        imagedestroy($img);
        
        clearstatcache(true, $webpPath);
        $newSize = filesize($webpPath);
        echo "saved as $webpName (" . round($size/1024) . "KB -> " . round($newSize/1024) . "KB)\n";
        
        if ($updateDb) {
            try {
                $pdo = new PDO('mysql:host=127.0.0.1;port=3306;dbname=musclecart_db', 'root', '');
                $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
                $stmt = $pdo->prepare("UPDATE products SET image_url = ? WHERE image_url = ?");
                $stmt->execute([$webpName, $name]);
                $rows = $stmt->rowCount();
                echo "  DB: $rows rows updated ($name -> $webpName)\n";
            } catch (Exception $e) {
                echo "  DB Error: " . $e->getMessage() . "\n";
            }
        }
        
        // Delete old file
        unlink($f);
        echo "  Deleted: $name\n";
    }
}

echo "=== Mobile Backend ===\n";
fixAndConvert($dir, true);

echo "\n=== Admin Backend ===\n";
if (is_dir($adminDir)) {
    fixAndConvert($adminDir, false);
} else {
    echo "Admin dir not found\n";
}

echo "\nDone! Verify files:\n";
$remaining = glob("$dir/*");
foreach ($remaining as $f) {
    clearstatcache(true, $f);
    echo "  " . basename($f) . " (" . round(filesize($f)/1024) . " KB)\n";
}
