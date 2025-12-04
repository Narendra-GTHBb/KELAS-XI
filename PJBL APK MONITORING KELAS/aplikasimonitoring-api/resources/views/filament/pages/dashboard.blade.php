<x-filament-panels::page>
    <style>
        .dashboard-grid {
            display: grid;
            gap: 1.5rem;
        }
        
        /* Header */
        .welcome-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 1rem;
            padding: 2rem;
            color: white;
        }
        
        .welcome-section h2 {
            font-size: 1.5rem;
            font-weight: 700;
            margin: 0 0 0.5rem 0;
        }
        
        .welcome-section p {
            margin: 0;
            opacity: 0.9;
        }
        
        /* Stats Row */
        .stats-row {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 1rem;
        }
        
        @media (max-width: 1024px) {
            .stats-row { grid-template-columns: repeat(2, 1fr); }
        }
        
        @media (max-width: 640px) {
            .stats-row { grid-template-columns: 1fr; }
        }
        
        .stat-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            display: flex;
            align-items: center;
            gap: 1rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            border-left: 4px solid;
        }
        
        .stat-card.blue { border-left-color: #3b82f6; }
        .stat-card.yellow { border-left-color: #f59e0b; }
        .stat-card.green { border-left-color: #10b981; }
        .stat-card.purple { border-left-color: #8b5cf6; }
        
        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 0.75rem;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .stat-card.blue .stat-icon { background: #dbeafe; color: #3b82f6; }
        .stat-card.yellow .stat-icon { background: #fef3c7; color: #f59e0b; }
        .stat-card.green .stat-icon { background: #d1fae5; color: #10b981; }
        .stat-card.purple .stat-icon { background: #ede9fe; color: #8b5cf6; }
        
        .stat-content h3 {
            font-size: 1.75rem;
            font-weight: 700;
            color: #1f2937;
            margin: 0;
        }
        
        .stat-content p {
            font-size: 0.875rem;
            color: #6b7280;
            margin: 0;
        }
        
        /* Content Grid */
        .content-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.5rem;
        }
        
        @media (max-width: 1024px) {
            .content-grid { grid-template-columns: 1fr; }
        }
        
        .card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .card h4 {
            font-size: 1rem;
            font-weight: 600;
            color: #1f2937;
            margin: 0 0 1.5rem 0;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        /* Kehadiran Stats */
        .kehadiran-stats {
            display: flex;
            justify-content: space-around;
            text-align: center;
            margin-bottom: 1.5rem;
        }
        
        .kehadiran-stat .number {
            font-size: 2rem;
            font-weight: 700;
        }
        
        .kehadiran-stat .number.green { color: #059669; }
        .kehadiran-stat .number.red { color: #dc2626; }
        .kehadiran-stat .number.blue { color: #2563eb; }
        
        .kehadiran-stat .label {
            font-size: 0.875rem;
            color: #6b7280;
        }
        
        /* Progress Bar */
        .progress-container {
            margin-top: 1rem;
        }
        
        .progress-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
            font-size: 0.875rem;
            color: #6b7280;
        }
        
        .progress-bar {
            height: 12px;
            background: #fee2e2;
            border-radius: 6px;
            overflow: hidden;
        }
        
        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #10b981, #34d399);
            border-radius: 6px;
            transition: width 0.3s;
        }
        
        /* Activity List */
        .activity-list {
            display: flex;
            flex-direction: column;
            gap: 0.75rem;
        }
        
        .activity-item {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 0.75rem;
            background: #f9fafb;
            border-radius: 0.5rem;
        }
        
        .activity-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 600;
            font-size: 0.875rem;
        }
        
        .activity-info { flex: 1; }
        
        .activity-name {
            font-weight: 600;
            color: #1f2937;
            font-size: 0.875rem;
        }
        
        .activity-detail {
            font-size: 0.75rem;
            color: #6b7280;
        }
        
        .activity-status {
            padding: 0.25rem 0.75rem;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 600;
        }
        
        .activity-status.masuk { background: #d1fae5; color: #059669; }
        .activity-status.tidak-masuk { background: #fee2e2; color: #dc2626; }
        
        .empty-state {
            text-align: center;
            color: #9ca3af;
            padding: 2rem;
        }
        
        /* Dark Mode */
        .dark .stat-card,
        .dark .card { background: #1f2937; }
        
        .dark .stat-content h3,
        .dark .card h4,
        .dark .activity-name { color: #f9fafb; }
        
        .dark .stat-content p,
        .dark .kehadiran-stat .label,
        .dark .activity-detail { color: #9ca3af; }
        
        .dark .activity-item { background: #374151; }
    </style>

    <div class="dashboard-grid">
        <!-- Welcome Section -->
        <div class="welcome-section">
            <h2>🏫 Aplikasi Monitoring Kelas</h2>
            <p>{{ $tanggalHariIni }}</p>
        </div>
        
        <!-- Stats Row - Data Master -->
        <div class="stats-row">
            <div class="stat-card blue">
                <div class="stat-icon">
                    <x-heroicon-o-user-group class="w-6 h-6" />
                </div>
                <div class="stat-content">
                    <h3>{{ $totalGuru }}</h3>
                    <p>Total Guru</p>
                </div>
            </div>
            
            <div class="stat-card yellow">
                <div class="stat-icon">
                    <x-heroicon-o-book-open class="w-6 h-6" />
                </div>
                <div class="stat-content">
                    <h3>{{ $totalMapel }}</h3>
                    <p>Mata Pelajaran</p>
                </div>
            </div>
            
            <div class="stat-card green">
                <div class="stat-icon">
                    <x-heroicon-o-academic-cap class="w-6 h-6" />
                </div>
                <div class="stat-content">
                    <h3>{{ $totalKelas }}</h3>
                    <p>Total Kelas</p>
                </div>
            </div>
            
            <div class="stat-card purple">
                <div class="stat-icon">
                    <x-heroicon-o-calendar-days class="w-6 h-6" />
                </div>
                <div class="stat-content">
                    <h3>{{ $totalJadwal }}</h3>
                    <p>Total Jadwal</p>
                </div>
            </div>
        </div>
        
        <!-- Content Grid -->
        <div class="content-grid">
            <!-- Statistik Kehadiran -->
            <div class="card">
                <h4>📊 Statistik Kehadiran Guru</h4>
                
                <div class="kehadiran-stats">
                    <div class="kehadiran-stat">
                        <div class="number green">{{ $totalMasuk }}</div>
                        <div class="label">Masuk</div>
                    </div>
                    <div class="kehadiran-stat">
                        <div class="number red">{{ $totalTidakMasuk }}</div>
                        <div class="label">Tidak Masuk</div>
                    </div>
                    <div class="kehadiran-stat">
                        <div class="number blue">{{ $totalKehadiran }}</div>
                        <div class="label">Total Record</div>
                    </div>
                </div>
                
                <div class="progress-container">
                    <div class="progress-header">
                        <span>Tingkat Kehadiran</span>
                        <span style="font-weight: 600; color: #059669;">{{ $persentaseMasuk }}%</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: {{ $persentaseMasuk }}%;"></div>
                    </div>
                </div>
            </div>
            
            <!-- Aktivitas Terbaru -->
            <div class="card">
                <h4>🕐 Aktivitas Terbaru</h4>
                
                <div class="activity-list">
                    @forelse($recentActivities as $activity)
                        <div class="activity-item">
                            <div class="activity-avatar">
                                {{ strtoupper(substr($activity->jadwal->guru->guru ?? 'G', 0, 2)) }}
                            </div>
                            <div class="activity-info">
                                <div class="activity-name">{{ $activity->jadwal->guru->guru ?? '-' }}</div>
                                <div class="activity-detail">
                                    {{ $activity->jadwal->mapel->mapel ?? '-' }} • {{ $activity->jadwal->kelas->kelas ?? '-' }}
                                </div>
                            </div>
                            <span class="activity-status {{ $activity->status == 'Masuk' ? 'masuk' : 'tidak-masuk' }}">
                                {{ $activity->status }}
                            </span>
                        </div>
                    @empty
                        <div class="empty-state">
                            Belum ada aktivitas kehadiran
                        </div>
                    @endforelse
                </div>
            </div>
        </div>
    </div>
</x-filament-panels::page>
