/**
 * Rocket Launch Mobile - Enhanced App Logic
 * Updated to use user's API format with richer data
 */

// ========================================
// THEME TOGGLE
// ========================================

const themeToggle = document.getElementById('theme-toggle');
const THEME_KEY = 'launch-tracker-theme';

function getSystemTheme() {
    return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark';
}

function getStoredTheme() {
    return localStorage.getItem(THEME_KEY);
}

function setTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem(THEME_KEY, theme);

    // Update meta theme-color
    const metaTheme = document.querySelector('meta[name="theme-color"]');
    if (metaTheme) {
        metaTheme.content = theme === 'light' ? '#F1F5F9' : '#0B0B10';
    }
}

function toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') || getSystemTheme();
    const next = current === 'dark' ? 'light' : 'dark';
    setTheme(next);
}

// Initialize theme
function initTheme() {
    const stored = getStoredTheme();
    if (stored) {
        setTheme(stored);
    }
    // If no stored preference, let CSS media query handle it

    themeToggle?.addEventListener('click', toggleTheme);

    // Listen for system theme changes
    window.matchMedia('(prefers-color-scheme: light)').addEventListener('change', (e) => {
        if (!getStoredTheme()) {
            // Only auto-switch if user hasn't manually set a preference
            document.documentElement.removeAttribute('data-theme');
        }
    });
}

initTheme();

// ========================================
// DOM ELEMENTS
// ========================================

const nextMission = document.getElementById('next-mission');
const nextDetails = document.getElementById('next-details');
const daysEl = document.getElementById('days');
const hoursEl = document.getElementById('hours');
const minutesEl = document.getElementById('minutes');
const secondsEl = document.getElementById('seconds');
const launchesEl = document.getElementById('launches');
const launchCountEl = document.getElementById('launch-count');

// State
let launches = [];
let countdownInterval = null;

// Mock data matching user's API format
const mockLaunches = [
    {
        id: "6232fdd6-872e-4146-aae9-4f3cca740512",
        slug: "falcon-9-block-5-starlink-9",
        name: "Falcon 9 Block 5 | Starlink 9",
        net: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString(),
        status: { name: "Go", abbrev: "GO" },
        launch_service_provider: { name: "SpaceX", type: "Commercial" },
        rocket: { configuration: { full_name: "Falcon 9 Block 5" } },
        pad: {
            name: "LC-39A",
            location: { name: "Kennedy Space Center, FL, USA", country_code: "USA" }
        },
        mission: {
            name: "Starlink Group 9",
            description: "Another batch of Starlink satellites for global broadband coverage."
        },
        image: "https://spacelaunchnow-prod-east.nyc3.digitaloceanspaces.com/media/launcher_images/falcon_9_block__image_20210506060831.jpg",
        url: "https://ll.thespacedevs.com/launch/6232fdd6/",
        vidURLs: [{ url: "https://www.youtube.com/watch?v=dQw4w9WgXcQ" }]
    },
    {
        id: "a1b2c3d4-5678-9012-efgh-ijklmnopqrst",
        slug: "crew-dragon-mission-11",
        name: "Falcon 9 Block 5 | Crew Dragon Mission 11",
        net: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString(),
        status: { name: "Go", abbrev: "GO" },
        launch_service_provider: { name: "SpaceX", type: "Commercial" },
        rocket: { configuration: { full_name: "Falcon 9 Block 5" } },
        pad: {
            name: "LC-39A",
            location: { name: "Kennedy Space Center, FL, USA", country_code: "USA" }
        },
        mission: {
            name: "Crew Dragon Mission 11",
            description: "NASA Commercial Crew rotation mission to the International Space Station."
        },
        image: "https://spacelaunchnow-prod-east.nyc3.digitaloceanspaces.com/media/launcher_images/falcon_9_block__image_20210506060831.jpg",
        url: "https://ll.thespacedevs.com/launch/a1b2c3d4/",
        vidURLs: [{ url: "https://www.youtube.com/watch?v=dQw4w9WgXcQ" }]
    },
    {
        id: "b2c3d4e5-6789-0123-fghi-jklmnopqrstu",
        slug: "atlas-v-nrol-165",
        name: "Atlas V 551 | NROL-165",
        net: new Date(Date.now() + 8 * 24 * 60 * 60 * 1000).toISOString(),
        status: { name: "TBD", abbrev: "TBD" },
        launch_service_provider: { name: "United Launch Alliance", type: "Commercial" },
        rocket: { configuration: { full_name: "Atlas V 551" } },
        pad: {
            name: "SLC-41",
            location: { name: "Cape Canaveral, FL, USA", country_code: "USA" }
        },
        mission: {
            name: "NROL-165",
            description: "Classified payload for the National Reconnaissance Office."
        },
        image: "https://spacelaunchnow-prod-east.nyc3.digitaloceanspaces.com/media/launcher_images/atlas_v_551_image_20210506060746.jpg",
        url: "https://ll.thespacedevs.com/launch/b2c3d4e5/",
        vidURLs: []
    },
    {
        id: "c3d4e5f6-7890-1234-ghij-klmnopqrstuv",
        slug: "starship-flight-9",
        name: "Starship | IFT-9",
        net: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString(),
        status: { name: "Go", abbrev: "GO" },
        launch_service_provider: { name: "SpaceX", type: "Commercial" },
        rocket: { configuration: { full_name: "Starship" } },
        pad: {
            name: "Orbital Launch Pad A",
            location: { name: "Starbase, TX, USA", country_code: "USA" }
        },
        mission: {
            name: "Integrated Flight Test 9",
            description: "Ninth integrated test flight of Starship and Super Heavy booster."
        },
        image: "https://spacelaunchnow-prod-east.nyc3.digitaloceanspaces.com/media/launcher_images/starship_image_20220503065729.jpg",
        url: "https://ll.thespacedevs.com/launch/c3d4e5f6/",
        vidURLs: [{ url: "https://www.youtube.com/watch?v=dQw4w9WgXcQ" }]
    },
    {
        id: "d4e5f6g7-8901-2345-hijk-lmnopqrstuvw",
        slug: "ariane-6-maiden",
        name: "Ariane 6 | OneWeb 21",
        net: new Date(Date.now() + 21 * 24 * 60 * 60 * 1000).toISOString(),
        status: { name: "Go", abbrev: "GO" },
        launch_service_provider: { name: "Arianespace", type: "Commercial" },
        rocket: { configuration: { full_name: "Ariane 64" } },
        pad: {
            name: "ELA-4",
            location: { name: "Kourou, French Guiana", country_code: "GUF" }
        },
        mission: {
            name: "OneWeb 21",
            description: "Batch of OneWeb broadband internet satellites to low Earth orbit."
        },
        image: "https://spacelaunchnow-prod-east.nyc3.digitaloceanspaces.com/media/launcher_images/ariane_6_image_20230712203756.jpg",
        url: "https://ll.thespacedevs.com/launch/d4e5f6g7/",
        vidURLs: []
    },
    {
        id: "e5f6g7h8-9012-3456-ijkl-mnopqrstuvwx",
        slug: "falcon-heavy-europa",
        name: "Falcon Heavy | Europa Clipper",
        net: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
        status: { name: "Go", abbrev: "GO" },
        launch_service_provider: { name: "SpaceX", type: "Commercial" },
        rocket: { configuration: { full_name: "Falcon Heavy" } },
        pad: {
            name: "LC-39A",
            location: { name: "Kennedy Space Center, FL, USA", country_code: "USA" }
        },
        mission: {
            name: "Europa Clipper",
            description: "NASA mission to explore Jupiter's moon Europa for signs of habitability."
        },
        image: "https://spacelaunchnow-prod-east.nyc3.digitaloceanspaces.com/media/launcher_images/falcon_heavy_image_20210430232025.jpg",
        url: "https://ll.thespacedevs.com/launch/e5f6g7h8/",
        vidURLs: [{ url: "https://www.youtube.com/watch?v=dQw4w9WgXcQ" }]
    }
];

/**
 * Transform API response to internal format
 */
function transformLaunch(data) {
    return {
        id: data.id,
        name: data.name,
        date: data.net,
        status: data.status?.abbrev || 'TBD',
        statusName: data.status?.name || 'To Be Determined',
        provider: data.launch_service_provider?.name || 'Unknown',
        providerType: data.launch_service_provider?.type || 'Unknown',
        rocket: data.rocket?.configuration?.full_name || 'Unknown Rocket',
        padName: data.pad?.name || 'TBD',
        padLocation: data.pad?.location?.name || 'Unknown Location',
        country: data.pad?.location?.country_code || '',
        missionName: data.mission?.name || data.name,
        missionDesc: data.mission?.description || 'Mission details coming soon.',
        image: data.image || null,
        url: data.url || '#',
        videoUrl: data.vidURLs?.[0]?.url || null
    };
}

/**
 * Fetch launches - use mock data for now
 * Replace with actual API endpoint when available
 */
async function fetchLaunches() {
    // TODO: Replace with actual API endpoint
    // try {
    //     const response = await fetch('YOUR_API_ENDPOINT');
    //     const data = await response.json();
    //     launches = data.map(transformLaunch)
    //         .sort((a, b) => new Date(a.date) - new Date(b.date));
    // } catch (e) {
    //     console.warn('Using mock data:', e.message);
    //     launches = mockLaunches.map(transformLaunch);
    // }

    // Using mock data for demonstration
    launches = mockLaunches.map(transformLaunch);
    return launches;
}

/**
 * Format date for mobile display
 */
function formatDate(dateStr) {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', {
        weekday: 'short',
        month: 'short',
        day: 'numeric'
    });
}

/**
 * Format time
 */
function formatTime(dateStr) {
    const d = new Date(dateStr);
    return d.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        timeZoneName: 'short'
    });
}

/**
 * Get time remaining
 */
function getTimeRemaining(launchDate) {
    const diff = new Date(launchDate).getTime() - Date.now();
    if (diff <= 0) return { days: 0, hours: 0, minutes: 0, seconds: 0, total: 0 };

    return {
        days: Math.floor(diff / (1000 * 60 * 60 * 24)),
        hours: Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
        minutes: Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60)),
        seconds: Math.floor((diff % (1000 * 60)) / 1000),
        total: diff
    };
}

/**
 * Pad number
 */
function pad(n) {
    return String(n).padStart(2, '0');
}

/**
 * Format countdown for cards
 */
function formatCountdown(t) {
    if (t.total <= 0) return 'Launched!';
    if (t.days > 0) return `T-${t.days}d ${t.hours}h`;
    if (t.hours > 0) return `T-${t.hours}h ${t.minutes}m`;
    return `T-${t.minutes}m ${t.seconds}s`;
}

/**
 * Get status color class
 */
function getStatusClass(status) {
    switch (status.toUpperCase()) {
        case 'GO':
            return 'status-badge--go';
        case 'TBC':
            return 'status-badge--tbc';
        case 'TBD':
            return 'status-badge--tbd';
        case 'HOLD':
            return 'status-badge--hold';
        default:
            return 'status-badge--tbd';
    }
}

/**
 * Update hero countdown
 */
function updateCountdown() {
    if (!launches.length) return;

    const t = getTimeRemaining(launches[0].date);
    daysEl.textContent = pad(t.days);
    hoursEl.textContent = pad(t.hours);
    minutesEl.textContent = pad(t.minutes);
    secondsEl.textContent = pad(t.seconds);
}

/**
 * Render hero
 */
function renderHero() {
    if (!launches.length) {
        nextMission.textContent = 'No Launches';
        nextDetails.textContent = 'Check back later';
        return;
    }

    const next = launches[0];
    nextMission.textContent = next.missionName;
    nextDetails.textContent = `${next.rocket} • ${next.provider}`;

    updateCountdown();
    if (countdownInterval) clearInterval(countdownInterval);
    countdownInterval = setInterval(updateCountdown, 1000);
}

/**
 * SVG Icons
 */
const icons = {
    rocket: `<svg class="card-icon-placeholder" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z"/>
        <path d="m12 15-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"/>
    </svg>`,
    calendar: `<svg class="card-detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <rect x="3" y="4" width="18" height="18" rx="2"/>
        <line x1="16" y1="2" x2="16" y2="6"/>
        <line x1="8" y1="2" x2="8" y2="6"/>
        <line x1="3" y1="10" x2="21" y2="10"/>
    </svg>`,
    clock: `<svg class="card-detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"/>
        <polyline points="12 6 12 12 16 14"/>
    </svg>`,
    location: `<svg class="card-detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
        <circle cx="12" cy="10" r="3"/>
    </svg>`,
    provider: `<svg class="card-detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
        <polyline points="9 22 9 12 15 12 15 22"/>
    </svg>`,
    external: `<svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
        <polyline points="15 3 21 3 21 9"/>
        <line x1="10" y1="14" x2="21" y2="3"/>
    </svg>`,
    info: `<svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"/>
        <line x1="12" y1="16" x2="12" y2="12"/>
        <line x1="12" y1="8" x2="12.01" y2="8"/>
    </svg>`,
    youtube: `<svg class="btn-icon" viewBox="0 0 24 24" fill="currentColor">
        <path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/>
    </svg>`
};

/**
 * Truncate text
 */
function truncate(text, maxLen) {
    if (!text) return '';
    return text.length > maxLen ? text.substring(0, maxLen) + '...' : text;
}

/**
 * Create launch card with enhanced info
 */
function createCard(launch) {
    const t = getTimeRemaining(launch.date);
    const isUpcoming = t.total > 0;

    const card = document.createElement('article');
    card.className = 'launch-card';
    card.setAttribute('tabindex', '0');

    card.innerHTML = `
        <div class="card-image">
            ${launch.image
            ? `<img src="${launch.image}" alt="${launch.missionName}" loading="lazy" onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                   <div class="card-image-fallback" style="display:none">${icons.rocket}</div>`
            : `<div class="card-image-fallback">${icons.rocket}</div>`
        }
            <div class="card-status ${getStatusClass(launch.status)}">
                ${launch.status}
            </div>
        </div>
        
        <div class="card-body">
            <h3 class="card-title">${launch.missionName}</h3>
            <p class="card-subtitle">${launch.rocket}</p>
            <p class="card-description">${truncate(launch.missionDesc, 80)}</p>
            
            <div class="card-meta">
                <div class="card-meta-item">
                    ${icons.provider}
                    <span>${launch.provider}</span>
                </div>
                <div class="card-meta-item">
                    ${icons.location}
                    <span>${launch.padName}</span>
                </div>
                <div class="card-meta-item">
                    ${icons.calendar}
                    <span>${formatDate(launch.date)}</span>
                </div>
                <div class="card-meta-item">
                    ${icons.clock}
                    <span>${formatTime(launch.date)}</span>
                </div>
            </div>
            
            <div class="card-footer">
                <span class="card-countdown ${isUpcoming ? '' : 'card-countdown--past'}">${formatCountdown(t)}</span>
                <span class="card-location">${launch.padLocation}</span>
            </div>
            
            <div class="card-actions">
                <button class="action-btn action-btn--info" data-url="${launch.url}" aria-label="More info">
                    ${icons.info}
                    <span>More Info</span>
                </button>
                <button class="action-btn action-btn--youtube ${!launch.videoUrl ? 'action-btn--disabled' : ''}" 
                        data-url="${launch.videoUrl || ''}" 
                        aria-label="Watch on YouTube"
                        ${!launch.videoUrl ? 'disabled' : ''}>
                    ${icons.youtube}
                    <span>${launch.videoUrl ? 'Watch Live' : 'No Stream'}</span>
                </button>
            </div>
        </div>
    `;

    // Add button event listeners
    const infoBtn = card.querySelector('.action-btn--info');
    const youtubeBtn = card.querySelector('.action-btn--youtube');

    infoBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        if (launch.url && launch.url !== '#') {
            window.open(launch.url, '_blank');
        }
    });

    if (launch.videoUrl) {
        youtubeBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            window.open(launch.videoUrl, '_blank');
        });
    }

    return card;
}

/**
 * Render all cards
 */
function renderCards() {
    launchesEl.innerHTML = '';

    if (!launches.length) {
        launchesEl.innerHTML = '<div class="empty"><p>No upcoming launches</p></div>';
        return;
    }

    launches.forEach(l => launchesEl.appendChild(createCard(l)));
    launchCountEl.textContent = launches.length;
}

/**
 * Initialize app
 */
async function init() {
    try {
        await fetchLaunches();
        renderHero();
        renderCards();
    } catch (e) {
        console.error('Init failed:', e);
        launchesEl.innerHTML = '<div class="empty"><p>Failed to load. Refresh to try again.</p></div>';
    }
}

document.addEventListener('DOMContentLoaded', init);
