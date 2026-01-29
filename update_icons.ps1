Add-Type -AssemblyName System.Drawing

$sourceFile = "flag.png"
$resDir = "app\src\main\res"

# Define icon sizes
$sizes = @{
    "mipmap-mdpi" = 48
    "mipmap-hdpi" = 72
    "mipmap-xhdpi" = 96
    "mipmap-xxhdpi" = 144
    "mipmap-xxxhdpi" = 192
}

# Ensure source exists
if (-not (Test-Path $sourceFile)) {
    Write-Error "Source file $sourceFile not found!"
    exit 1
}

$image = [System.Drawing.Image]::FromFile($sourceFile)

foreach ($folder in $sizes.Keys) {
    $size = $sizes[$folder]
    $targetDir = Join-Path $resDir $folder
    
    # Create directory if not exists
    if (-not (Test-Path $targetDir)) {
        New-Item -ItemType Directory -Path $targetDir | Out-Null
    }

    # Resize image
    $bitmap = New-Object System.Drawing.Bitmap $size, $size
    $graph = [System.Drawing.Graphics]::FromImage($bitmap)
    $graph.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graph.DrawImage($image, 0, 0, $size, $size)
    
    # Save as ic_launcher.png (Square/Legacy)
    $targetPath = Join-Path $targetDir "ic_launcher.png"
    $bitmap.Save($targetPath, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Generated $targetPath ($size x $size)"

    # Save as ic_launcher_round.png (Round - simplified, just resizing source for now)
    # In a real scenario, we'd mask it circle, but resizing is a good start.
    $targetPathRound = Join-Path $targetDir "ic_launcher_round.png"
    $bitmap.Save($targetPathRound, [System.Drawing.Imaging.ImageFormat]::Png)
    
    $bitmap.Dispose()
    $graph.Dispose()
}

$image.Dispose()
Write-Host "Icon generation complete!"
