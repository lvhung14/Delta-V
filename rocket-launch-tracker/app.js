/**
 * Rocket Launch Tracker - Application Logic
 * Fetches upcoming launches and manages countdown timers
 */

// DOM Elements
const nextMissionName = document.getElementById('next-mission-name');
const nextMissionDetails = document.getElementById('next-mission-details');
const daysEl = document.getElementById('days');
const hoursEl = document.getElementById('hours');
const minutesEl = document.getElementById('minutes');
const secondsEl = document.getElementById('seconds');
const launchesGrid = document.getElementById('launches-grid');
const totalLaunchesEl = document.getElementById('total-launches');

// State
let launches = [];
let countdownInterval = null;

// Fallback mock data (in case API fails)
const mockLaunches = [
    {
        id: '1',
        name: 'Starlink Group 12-5',
        rocket: 'Falcon 9',
        date_utc: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString(),
        launchpad: 'Kennedy Space Center',
        details: 'SpaceX Starlink satellite deployment mission',
        patch: null
    },
    {
        id: '2',
        name: 'Crew Dragon Mission 10',
        rocket: 'Falcon 9',
        date_utc: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString(),
        launchpad: 'Cape Canaveral',
        details: 'Crew rotation mission to the International Space Station',
        patch: null
    },
    {
        id: '3',
        name: 'NROL-165',
        rocket: 'Atlas V',
        date_utc: new Date(Date.now() + 8 * 24 * 60 * 60 * 1000).toISOString(),
        launchpad: 'Vandenberg SFB',
        details: 'National Reconnaissance Office classified payload',
        patch: null
    },
    {
        id: '4',
        name: 'Artemis III',
        rocket: 'SLS Block 1',
        date_utc: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString(),
        launchpad: 'Kennedy Space Center',
        details: 'First crewed lunar landing since Apollo 17',
        patch: null
    },
    {
        id: '5',
        name: 'Starship Flight 8',
        rocket: 'Starship',
        date_utc: new Date(Date.now() + 21 * 24 * 60 * 60 * 1000).toISOString(),
        launchpad: 'Starbase, Texas',
        details: 'Orbital test flight of fully reusable spacecraft',
        patch: null
    },
    {
        id: '6',
        name: 'Europa Clipper',
        rocket: 'Falcon Heavy',
        date_utc: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
        launchpad: 'Kennedy Space Center',
        details: 'Mission to explore Jupiter\'s moon Europa for signs of life',
        patch: null
    }
];

/**
 * Fetch upcoming launches from SpaceX API
 */
async function fetchLaunches() {
    try {
        const response = await fetch('https://api.spacexdata.com/v4/launches/upcoming');

        if (!response.ok) {
            throw new Error('API request failed');
        }

        const data = await response.json();

        // Transform API data to our format
        launches = data.map(launch => ({
            id: launch.id,
            name: launch.name,
            rocket: 'Falcon 9', // SpaceX API v4 doesn't include rocket name directly
            date_utc: launch.date_utc,
            launchpad: launch.launchpad || 'TBD',
            details: launch.details || 'Mission details coming soon',
            patch: launch.links?.patch?.small || null
        }));

        // Sort by date
        launches.sort((a, b) => new Date(a.date_utc) - new Date(b.date_utc));

    } catch (error) {
        console.warn('Using mock data:', error.message);
        launches = mockLaunches;
    }

    return launches;
}

/**
 * Format date for display
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    });
}

/**
 * Format time for display
 */
function formatTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        timeZoneName: 'short'
    });
}

/**
 * Calculate time remaining until launch
 */
function getTimeRemaining(launchDate) {
    const now = new Date().getTime();
    const launch = new Date(launchDate).getTime();
    const diff = launch - now;

    if (diff <= 0) {
        return { days: 0, hours: 0, minutes: 0, seconds: 0, total: 0 };
    }

    return {
        days: Math.floor(diff / (1000 * 60 * 60 * 24)),
        hours: Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
        minutes: Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60)),
        seconds: Math.floor((diff % (1000 * 60)) / 1000),
        total: diff
    };
}

/**
 * Format countdown for card display
 */
function formatCountdown(timeRemaining) {
    if (timeRemaining.total <= 0) {
        return 'Launched!';
    }

    if (timeRemaining.days > 0) {
        return `T-${timeRemaining.days}d ${timeRemaining.hours}h`;
    }

    return `T-${timeRemaining.hours}h ${timeRemaining.minutes}m`;
}

/**
 * Pad number with leading zero
 */
function padNumber(num) {
    return String(num).padStart(2, '0');
}

/**
 * Update the hero countdown timer
 */
function updateHeroCountdown() {
    if (launches.length === 0) return;

    const nextLaunch = launches[0];
    const time = getTimeRemaining(nextLaunch.date_utc);

    daysEl.textContent = padNumber(time.days);
    hoursEl.textContent = padNumber(time.hours);
    minutesEl.textContent = padNumber(time.minutes);
    secondsEl.textContent = padNumber(time.seconds);
}

/**
 * Render the hero section with next launch info
 */
function renderHero() {
    if (launches.length === 0) {
        nextMissionName.textContent = 'No Upcoming Launches';
        nextMissionDetails.textContent = 'Check back later for scheduled missions';
        return;
    }

    const nextLaunch = launches[0];
    nextMissionName.textContent = nextLaunch.name;
    nextMissionDetails.textContent = `${nextLaunch.rocket} • ${formatDate(nextLaunch.date_utc)} • ${formatTime(nextLaunch.date_utc)}`;

    // Start countdown
    updateHeroCountdown();
    if (countdownInterval) clearInterval(countdownInterval);
    countdownInterval = setInterval(updateHeroCountdown, 1000);
}

/**
 * Create SVG icon for rocket placeholder
 */
function getRocketIcon() {
    return `
        <svg class="launch-patch-placeholder" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z"/>
            <path d="m12 15-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"/>
            <path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0"/>
            <path d="M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5"/>
        </svg>
    `;
}

/**
 * Create calendar icon
 */
function getCalendarIcon() {
    return `
        <svg class="launch-detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
            <line x1="16" y1="2" x2="16" y2="6"/>
            <line x1="8" y1="2" x2="8" y2="6"/>
            <line x1="3" y1="10" x2="21" y2="10"/>
        </svg>
    `;
}

/**
 * Create location icon
 */
function getLocationIcon() {
    return `
        <svg class="launch-detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
            <circle cx="12" cy="10" r="3"/>
        </svg>
    `;
}

/**
 * Create clock icon
 */
function getClockIcon() {
    return `
        <svg class="launch-detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <polyline points="12 6 12 12 16 14"/>
        </svg>
    `;
}

/**
 * Create a launch card element
 */
function createLaunchCard(launch) {
    const timeRemaining = getTimeRemaining(launch.date_utc);
    const isUpcoming = timeRemaining.total > 0;

    const card = document.createElement('article');
    card.className = 'launch-card';
    card.setAttribute('role', 'article');
    card.setAttribute('aria-label', `${launch.name} launch details`);

    card.innerHTML = `
        <div class="launch-header">
            <div class="launch-patch">
                ${launch.patch
            ? `<img src="${launch.patch}" alt="${launch.name} mission patch" loading="lazy">`
            : getRocketIcon()
        }
            </div>
            <div class="launch-info">
                <h3 class="launch-name">${launch.name}</h3>
                <p class="launch-rocket">${launch.rocket}</p>
            </div>
        </div>
        
        <div class="launch-details">
            <div class="launch-detail-row">
                ${getCalendarIcon()}
                <span class="launch-detail-label">Date:</span>
                <span class="launch-detail-value">${formatDate(launch.date_utc)}</span>
            </div>
            <div class="launch-detail-row">
                ${getClockIcon()}
                <span class="launch-detail-label">Time:</span>
                <span class="launch-detail-value">${formatTime(launch.date_utc)}</span>
            </div>
            <div class="launch-detail-row">
                ${getLocationIcon()}
                <span class="launch-detail-label">Site:</span>
                <span class="launch-detail-value">${launch.launchpad}</span>
            </div>
        </div>
        
        <div class="launch-countdown">
            <span class="status-badge ${isUpcoming ? 'status-badge--upcoming' : 'status-badge--tbd'}">
                ${isUpcoming ? 'Scheduled' : 'TBD'}
            </span>
            <span class="launch-countdown-value">${formatCountdown(timeRemaining)}</span>
        </div>
    `;

    return card;
}

/**
 * Render all launch cards
 */
function renderLaunches() {
    // Clear loading state
    launchesGrid.innerHTML = '';

    if (launches.length === 0) {
        launchesGrid.innerHTML = `
            <div class="empty-state">
                <p>No upcoming launches scheduled at this time.</p>
            </div>
        `;
        return;
    }

    // Render each launch card
    launches.forEach(launch => {
        const card = createLaunchCard(launch);
        launchesGrid.appendChild(card);
    });

    // Update stats
    totalLaunchesEl.textContent = launches.length;
}

/**
 * Initialize the application
 */
async function init() {
    try {
        await fetchLaunches();
        renderHero();
        renderLaunches();
    } catch (error) {
        console.error('Failed to initialize:', error);
        launchesGrid.innerHTML = `
            <div class="empty-state">
                <p>Failed to load launches. Please refresh the page.</p>
            </div>
        `;
    }
}

// Start the app when DOM is ready
document.addEventListener('DOMContentLoaded', init);
