# Test Mobile Login API
$body = @{
    email = "test@test.com"
    password = "password123"
} | ConvertTo-Json

Write-Host "Testing Mobile Login API..." -ForegroundColor Cyan
Write-Host "Endpoint: http://127.0.0.1:8000/api/v1/login" -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "http://127.0.0.1:8000/api/v1/login" -Method Post -Body $body -ContentType "application/json"
    
    Write-Host "✓ Login Success!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Response:" -ForegroundColor Yellow
    $response | ConvertTo-Json -Depth 5
    
    Write-Host ""
    Write-Host "Token: $($response.data.token)" -ForegroundColor Green
    Write-Host "User: $($response.data.user.name) ($($response.data.user.email))" -ForegroundColor Green
    
} catch {
    Write-Host "✗ Login Failed!" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
}
