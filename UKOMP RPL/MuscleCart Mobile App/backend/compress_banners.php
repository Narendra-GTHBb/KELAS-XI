<?php
$bannerDir = __DIR__ . '/public/images/banners';
$files = scandir($bannerDir);
foreach ($files as $f) {
    if ($f === '.' || $f === '..') continue;
    $path = $bannerDir . '/' . $f;
    $size = filesize($path);
    if ($size < 100 * 1024) {
        echo "$f: " . round($size/1024) . "KB - already small\n";
        continue;
    }
    $ext = strtolower(pathinfo($f, PATHINFO_EXTENSION));
    $img = null;
    if ($ext === 'jpg' || $ext === 'jpeg') $img = imagecreatefromjpeg($path);
    elseif ($ext === 'png') $img = imagecreatefrompng($path);
    elseif ($ext === 'webp') $img = imagecreatefromwebp($path);
    
    if (!$img) {
        // Try generic loader
        $img = imagecreatefromstring(file_get_contents($path));
    }
    if (!$img) {
        echo "$f: cannot load\n";
        continue;
    }
    
    $w = imagesx($img);
    $h = imagesy($img);
    $max = 800;
    if ($w > $max || $h > $max) {
        $ratio = min($max/$w, $max/$h);
        $nw = (int)($w*$ratio);
        $nh = (int)($h*$ratio);
        $r = imagecreatetruecolor($nw, $nh);
        imagecopyresampled($r, $img, 0, 0, 0, 0, $nw, $nh, $w, $h);
        imagedestroy($img);
        $img = $r;
        echo "$f: resized {$w}x{$h} -> {$nw}x{$nh}\n";
    }
    imagejpeg($img, $path, 75);
    imagedestroy($img);
    clearstatcache(true, $path);
    $newSize = filesize($path);
    echo "$f: " . round($size/1024) . "KB -> " . round($newSize/1024) . "KB\n";
}
echo "Done!\n";
