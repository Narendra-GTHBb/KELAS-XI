<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Redirect to Admin Panel</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            background: white;
            padding: 50px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 600px;
            animation: fadeIn 0.5s;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .icon {
            font-size: 80px;
            margin-bottom: 20px;
        }
        h1 {
            color: #333;
            margin-bottom: 20px;
            font-size: 28px;
        }
        p {
            color: #666;
            margin-bottom: 30px;
            font-size: 16px;
            line-height: 1.6;
        }
        .info-box {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
            border-left: 4px solid #667eea;
        }
        .info-box strong {
            color: #667eea;
            font-size: 18px;
        }
        .port-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin: 30px 0;
        }
        .port-card {
            padding: 20px;
            background: #f8f9fa;
            border-radius: 10px;
            border: 2px solid #e0e0e0;
        }
        .port-card.active {
            border-color: #667eea;
            background: #ede7f6;
        }
        .port-card h3 {
            color: #667eea;
            margin-bottom: 10px;
        }
        .port-card p {
            margin: 5px 0;
            font-size: 14px;
        }
        .btn {
            display: inline-block;
            padding: 15px 40px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-decoration: none;
            border-radius: 50px;
            font-weight: bold;
            font-size: 16px;
            transition: transform 0.3s, box-shadow 0.3s;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
        }
        .countdown {
            margin-top: 20px;
            color: #999;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="icon">🚀</div>
        <h1>Port Yang Salah!</h1>
        <p>Anda sedang mengakses <strong>Port 8000</strong> yang digunakan untuk <strong>Mobile API Backend</strong>.</p>
        
        <div class="port-info">
            <div class="port-card">
                <h3>Port 8000</h3>
                <p>📱 Mobile API</p>
                <p>Untuk Android App</p>
                <p><small>/api/v1/products</small></p>
            </div>
            <div class="port-card active">
                <h3>Port 8001</h3>
                <p>🌐 Admin Panel</p>
                <p>Untuk Web Dashboard</p>
                <p><small>/admin</small></p>
            </div>
        </div>

        <div class="info-box">
            <strong>Admin Panel tersedia di:</strong>
            <p style="margin-top: 10px; font-family: monospace; font-size: 18px; color: #667eea;">
                http://127.0.0.1:8001/admin
            </p>
        </div>

        <a href="http://127.0.0.1:8001/admin" class="btn">
            Buka Admin Panel 👉
        </a>

        <div class="countdown">
            <p>Auto redirect dalam <span id="countdown">5</span> detik...</p>
        </div>
    </div>

    <script>
        let seconds = 5;
        const countdownEl = document.getElementById('countdown');
        
        const interval = setInterval(() => {
            seconds--;
            countdownEl.textContent = seconds;
            
            if (seconds <= 0) {
                clearInterval(interval);
                window.location.href = 'http://127.0.0.1:8001/admin';
            }
        }, 1000);
    </script>
</body>
</html>
