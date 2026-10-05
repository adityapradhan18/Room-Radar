/**
 * Room Radar - Frontend Application Logic
 * Vanilla JavaScript implementation communicating with Spring Boot 3 REST API.
 */

// API Configuration
const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  ? 'http://localhost:8080/api/rooms'
  : '/api/rooms';

// Application State
const state = {
  currentDay: 'MON',
  currentStart: '10:00',
  currentEnd: '11:00',
  rooms: [],
  selectedRoom: null,
  activeFiltersCount: 0
};

// DOM Elements
const elements = {
  // Inputs & Controls
  daySelect: document.getElementById('daySelect'),
  startTime: document.getElementById('startTime'),
  endTime: document.getElementById('endTime'),
  filterBuilding: document.getElementById('filterBuilding'),
  filterType: document.getElementById('filterType'),
  filterFloor: document.getElementById('filterFloor'),
  filterCapacity: document.getElementById('filterCapacity'),
  btnFindRooms: document.getElementById('btnFindRooms'),
  btnFreeNow: document.getElementById('btnFreeNow'),
  btnResetFilters: document.getElementById('btnResetFilters'),
  toggleFiltersBtn: document.getElementById('toggleFiltersBtn'),
  filtersBody: document.getElementById('filtersBody'),
  activeFilterBadge: document.getElementById('activeFilterBadge'),
  sortSelect: document.getElementById('sortSelect'),
  periodChips: document.querySelectorAll('.period-chip'),

  // Feedback & Status
  serverStatus: document.getElementById('serverStatus'),
  statusLabel: document.getElementById('statusLabel'),
  statusDot: document.querySelector('.status-dot'),
  alertBanner: document.getElementById('alertBanner'),
  alertTitle: document.getElementById('alertTitle'),
  alertMessage: document.getElementById('alertMessage'),
  alertCloseBtn: document.getElementById('alertCloseBtn'),

  // Results Containers
  resultsSubtext: document.getElementById('resultsSubtext'),
  roomsGrid: document.getElementById('roomsGrid'),
  loadingState: document.getElementById('loadingState'),
  emptyState: document.getElementById('emptyState'),
  emptyClearBtn: document.getElementById('emptyClearBtn'),

  // Schedule Modal
  scheduleModal: document.getElementById('scheduleModal'),
  modalCloseBtn: document.getElementById('modalCloseBtn'),
  modalCloseActionBtn: document.getElementById('modalCloseActionBtn'),
  modalTitle: document.getElementById('modalTitle'),
  modalSubtitle: document.getElementById('modalSubtitle'),
  modalTypeBadge: document.getElementById('modalTypeBadge'),
  modalCapacityBadge: document.getElementById('modalCapacityBadge'),
  timelineTrack: document.getElementById('timelineTrack'),
  scheduleEntriesContainer: document.getElementById('scheduleEntriesContainer')
};

// ============================================================================
// Initialization & Event Listeners
// ============================================================================
document.addEventListener('DOMContentLoaded', () => {
  setupEventListeners();
  checkServerConnection();
  
  // Trigger initial search for default Demo window: Monday 10:00 - 11:00
  fetchFreeRooms();
});

function setupEventListeners() {
  // Primary Action Buttons
  elements.btnFindRooms.addEventListener('click', () => fetchFreeRooms());
  elements.btnFreeNow.addEventListener('click', () => fetchFreeRoomsNow());
  elements.btnResetFilters.addEventListener('click', () => resetFilters());
  elements.emptyClearBtn.addEventListener('click', () => {
    resetFilters();
    fetchFreeRooms();
  });

  // Filter Toggle
  elements.toggleFiltersBtn.addEventListener('click', () => {
    const isCollapsed = elements.filtersBody.classList.toggle('collapsed');
    elements.toggleFiltersBtn.setAttribute('aria-expanded', !isCollapsed);
    const chevron = elements.toggleFiltersBtn.querySelector('.chevron-icon');
    if (chevron) {
      chevron.style.transform = isCollapsed ? 'rotate(-90deg)' : 'rotate(0deg)';
    }
  });

  // Input Change Listeners
  [elements.filterBuilding, elements.filterType, elements.filterFloor, elements.filterCapacity].forEach(input => {
    input.addEventListener('change', () => {
      updateActiveFilterCount();
      fetchFreeRooms();
    });
  });

  // Period Chips
  elements.periodChips.forEach(chip => {
    chip.addEventListener('click', () => {
      elements.periodChips.forEach(c => c.classList.remove('active-chip'));
      chip.classList.add('active-chip');
      elements.startTime.value = chip.dataset.start;
      elements.endTime.value = chip.dataset.end;
      fetchFreeRooms();
    });
  });

  // Time / Day change sync
  elements.daySelect.addEventListener('change', () => fetchFreeRooms());
  elements.startTime.addEventListener('change', () => syncPeriodChips());
  elements.endTime.addEventListener('change', () => syncPeriodChips());

  // Sorting
  elements.sortSelect.addEventListener('change', () => sortAndRenderCards());

  // Alert Dismiss
  elements.alertCloseBtn.addEventListener('click', () => hideAlert());

  // Modal Close
  elements.modalCloseBtn.addEventListener('click', () => elements.scheduleModal.close());
  elements.modalCloseActionBtn.addEventListener('click', () => elements.scheduleModal.close());
  
  // Close modal when clicking outside backdrop
  elements.scheduleModal.addEventListener('click', (e) => {
    const rect = elements.scheduleModal.getBoundingClientRect();
    const isInDialog = (rect.top <= e.clientY && e.clientY <= rect.top + rect.height &&
                        rect.left <= e.clientX && e.clientX <= rect.left + rect.width);
    if (!isInDialog) {
      elements.scheduleModal.close();
    }
  });
}

// ============================================================================
// Server Connection & Health Check
// ============================================================================
async function checkServerConnection() {
  try {
    const res = await fetch(`${API_BASE_URL}`, { method: 'GET' });
    if (res.ok) {
      setServerStatus('connected', 'API Connected (8080)');
    } else {
      setServerStatus('disconnected', `API Error (${res.status})`);
    }
  } catch (err) {
    setServerStatus('disconnected', 'API Offline (localhost:8080)');
  }
}

function setServerStatus(status, label) {
  elements.statusDot.className = `status-dot ${status}`;
  elements.statusLabel.textContent = label;
}

// ============================================================================
// Data Fetching: Free Rooms & Free Now
// ============================================================================

/**
 * 1. GET /api/rooms/free?day=MON&start=10:00&end=11:00
 */
async function fetchFreeRooms() {
  hideAlert();

  const day = elements.daySelect.value;
  const start = elements.startTime.value;
  const end = elements.endTime.value;

  // Client-side quick check
  if (!start || !end) {
    showAlert('Missing Time', 'Please choose both a start time and an end time.');
    return;
  }
  if (start >= end) {
    showAlert('Invalid Time Window', `End time (${end}) must be strictly after start time (${start}).`);
    return;
  }

  state.currentDay = day;
  state.currentStart = start;
  state.currentEnd = end;

  // Build query string
  const params = new URLSearchParams({
    day: day,
    start: start,
    end: end
  });

  if (elements.filterBuilding.value) params.append('building', elements.filterBuilding.value);
  if (elements.filterType.value) params.append('roomType', elements.filterType.value);
  if (elements.filterFloor.value) params.append('floor', elements.filterFloor.value);
  if (elements.filterCapacity.value) params.append('minCapacity', elements.filterCapacity.value);

  showLoading(true);

  try {
    const response = await fetch(`${API_BASE_URL}/free?${params.toString()}`);
    
    if (!response.ok) {
      const errData = await response.json().catch(() => ({}));
      throw new Error(errData.message || `Server responded with status ${response.status}`);
    }

    const rooms = await response.json();
    setServerStatus('connected', 'API Connected (8080)');
    state.rooms = rooms;
    
    updateResultsHeader(rooms.length, day, start, end);
    sortAndRenderCards();
  } catch (error) {
    handleFetchError(error);
  } finally {
    showLoading(false);
  }
}

/**
 * 2. GET /api/rooms/free-now
 */
async function fetchFreeRoomsNow() {
  hideAlert();
  showLoading(true);

  const params = new URLSearchParams();
  if (elements.filterBuilding.value) params.append('building', elements.filterBuilding.value);
  if (elements.filterType.value) params.append('roomType', elements.filterType.value);
  if (elements.filterFloor.value) params.append('floor', elements.filterFloor.value);
  if (elements.filterCapacity.value) params.append('minCapacity', elements.filterCapacity.value);

  try {
    const response = await fetch(`${API_BASE_URL}/free-now?${params.toString()}`);
    
    if (!response.ok) {
      const errData = await response.json().catch(() => ({}));
      throw new Error(errData.message || `Server responded with status ${response.status}`);
    }

    // Read server headers if available to synchronize UI time inputs
    const serverDay = response.headers.get('X-Server-Day');
    const windowStart = response.headers.get('X-Window-Start');
    const windowEnd = response.headers.get('X-Window-End');

    if (serverDay) {
      const mappedDay = mapDayNameToEnum(serverDay);
      if (mappedDay) {
        elements.daySelect.value = mappedDay;
        state.currentDay = mappedDay;
      }
    }
    if (windowStart) {
      elements.startTime.value = windowStart;
      state.currentStart = windowStart;
    }
    if (windowEnd) {
      elements.endTime.value = windowEnd;
      state.currentEnd = windowEnd;
    }

    syncPeriodChips();

    const rooms = await response.json();
    setServerStatus('connected', 'API Connected (8080)');
    state.rooms = rooms;

    updateResultsHeader(rooms.length, state.currentDay, state.currentStart, state.currentEnd, true);
    sortAndRenderCards();
  } catch (error) {
    handleFetchError(error);
  } finally {
    showLoading(false);
  }
}

// ============================================================================
// Render Room Cards
// ============================================================================

function sortAndRenderCards() {
  const sortBy = elements.sortSelect.value;
  const sorted = [...state.rooms];

  sorted.sort((a, b) => {
    switch (sortBy) {
      case 'capacityDesc':
        return b.capacity - a.capacity;
      case 'capacityAsc':
        return a.capacity - b.capacity;
      case 'freeUntil':
        if (a.freeUntil === 'Rest of the day') return 1;
        if (b.freeUntil === 'Rest of the day') return -1;
        return a.freeUntil.localeCompare(b.freeUntil);
      case 'roomNumber':
      default:
        return a.roomNumber.localeCompare(b.roomNumber, undefined, { numeric: true });
    }
  });

  renderCards(sorted);
}

function renderCards(rooms) {
  elements.roomsGrid.innerHTML = '';

  if (!rooms || rooms.length === 0) {
    elements.emptyState.classList.remove('hidden');
    return;
  }

  elements.emptyState.classList.add('hidden');

  const fragment = document.createDocumentFragment();

  rooms.forEach(room => {
    const card = document.createElement('article');
    card.className = 'room-card';
    card.setAttribute('tabindex', '0');
    card.setAttribute('role', 'button');
    card.setAttribute('aria-label', `Room ${room.roomNumber}, ${room.roomType}, free until ${room.freeUntil}`);

    // Free until label formatting
    let freeBadgeContent = '';
    if (room.freeUntil === 'Rest of the day') {
      freeBadgeContent = `✨ Free for the rest of the day`;
    } else {
      const durationNote = room.minutesUntilNextClass ? ` (${formatDuration(room.minutesUntilNextClass)})` : '';
      freeBadgeContent = `⚡ Free until ${room.freeUntil}${durationNote}`;
    }

    card.innerHTML = `
      <div class="card-top">
        <div class="room-number-wrap">
          <span class="room-number">${escapeHtml(room.roomNumber)}</span>
          <span class="room-location">
            <svg class="detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
            ${escapeHtml(room.building)} &bull; Floor ${room.floor}
          </span>
        </div>
        <span class="badge-type ${room.roomType}">${formatRoomType(room.roomType)}</span>
      </div>

      <div class="card-details">
        <div class="detail-item" title="Room capacity">
          <svg class="detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
          <span>${room.capacity} seats</span>
        </div>
        <div class="detail-item" title="Floor level">
          <svg class="detail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="2" x2="12" y2="22"></line>
            <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
          </svg>
          <span>Floor ${room.floor}</span>
        </div>
      </div>

      <div class="free-until-banner">
        <div class="pulse-dot-green"></div>
        <span class="free-until-text">${freeBadgeContent}</span>
      </div>

      <div class="card-footer">
        <span class="schedule-action-text">
          View full schedule
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <line x1="5" y1="12" x2="19" y2="12"></line>
            <polyline points="12 5 19 12 12 19"></polyline>
          </svg>
        </span>
      </div>
    `;

    // Click handler to open schedule
    card.addEventListener('click', () => openRoomSchedule(room));
    card.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        openRoomSchedule(room);
      }
    });

    fragment.appendChild(card);
  });

  elements.roomsGrid.appendChild(fragment);
}

// ============================================================================
// Schedule Modal & Visual Timeline
// ============================================================================

/**
 * 4. GET /api/rooms/{id}/schedule?day=MON
 */
async function openRoomSchedule(room) {
  state.selectedRoom = room;
  const day = state.currentDay;

  // Set modal header details
  elements.modalTitle.textContent = `Room ${room.roomNumber} Schedule`;
  elements.modalSubtitle.textContent = `${room.building} • Floor ${room.floor} • ${formatDayName(day)} Schedule`;
  elements.modalTypeBadge.className = `badge-type ${room.roomType}`;
  elements.modalTypeBadge.textContent = formatRoomType(room.roomType);
  elements.modalCapacityBadge.textContent = `${room.capacity} seats`;

  elements.scheduleEntriesContainer.innerHTML = '<p class="schedule-empty-state">Loading schedule...</p>';
  elements.timelineTrack.innerHTML = '';

  elements.scheduleModal.showModal();

  try {
    const response = await fetch(`${API_BASE_URL}/${room.id}/schedule?day=${day}`);
    if (!response.ok) {
      throw new Error(`Failed to load schedule for room ${room.roomNumber}`);
    }

    const schedule = await response.json();
    renderScheduleEntries(schedule);
    renderTimeline(schedule);
  } catch (error) {
    elements.scheduleEntriesContainer.innerHTML = `
      <div class="schedule-empty-state" style="color: var(--danger);">
        <p>⚠️ Unable to fetch timetable: ${escapeHtml(error.message)}</p>
      </div>
    `;
  }
}

function renderScheduleEntries(schedule) {
  elements.scheduleEntriesContainer.innerHTML = '';

  if (!schedule || schedule.length === 0) {
    elements.scheduleEntriesContainer.innerHTML = `
      <div class="schedule-empty-state">
        <p>🎉 No classes scheduled on this day! This room is free all day.</p>
      </div>
    `;
    return;
  }

  const fragment = document.createDocumentFragment();

  schedule.forEach(entry => {
    const item = document.createElement('div');
    item.className = 'schedule-entry-item';

    const durationMin = calculateDurationMinutes(entry.startTime, entry.endTime);

    item.innerHTML = `
      <div class="entry-time-col">
        <span class="entry-time-range">${entry.startTime} - ${entry.endTime}</span>
        <span class="entry-duration">${durationMin} mins</span>
      </div>
      <div class="entry-info-col">
        <div class="entry-subject">${escapeHtml(entry.subject)}</div>
        <div class="entry-meta">
          <span>👨‍🏫 ${escapeHtml(entry.faculty)}</span>
        </div>
      </div>
      <span class="entry-batch-chip">${escapeHtml(entry.batch)}</span>
    `;

    fragment.appendChild(item);
  });

  elements.scheduleEntriesContainer.appendChild(fragment);
}

/**
 * Builds a visual 09:00 - 17:00 timeline bar highlighting busy classes and free gaps.
 */
function renderTimeline(schedule) {
  elements.timelineTrack.innerHTML = '';

  const dayStartMinutes = 9 * 60;   // 09:00 -> 540 min
  const dayEndMinutes = 17 * 60;    // 17:00 -> 1020 min
  const totalDayMinutes = dayEndMinutes - dayStartMinutes; // 480 min

  let currentPointer = dayStartMinutes;

  schedule.forEach(entry => {
    const classStart = timeToMinutes(entry.startTime);
    const classEnd = timeToMinutes(entry.endTime);

    // If there is free time before this class
    if (classStart > currentPointer) {
      const freeDuration = classStart - currentPointer;
      const freePct = (freeDuration / totalDayMinutes) * 100;
      const freeBlock = document.createElement('div');
      freeBlock.className = 'timeline-block free';
      freeBlock.style.width = `${freePct}%`;
      freeBlock.title = `Free: ${minutesToTime(currentPointer)} - ${minutesToTime(classStart)}`;
      elements.timelineTrack.appendChild(freeBlock);
    }

    // Occupied class block
    const classDuration = classEnd - classStart;
    const classPct = (classDuration / totalDayMinutes) * 100;
    const busyBlock = document.createElement('div');
    busyBlock.className = 'timeline-block busy';
    busyBlock.style.width = `${classPct}%`;
    busyBlock.title = `Busy: ${entry.startTime} - ${entry.endTime} (${entry.subject})`;
    elements.timelineTrack.appendChild(busyBlock);

    currentPointer = classEnd;
  });

  // If there is free time remaining until 17:00
  if (currentPointer < dayEndMinutes) {
    const remainingDuration = dayEndMinutes - currentPointer;
    const remainingPct = (remainingDuration / totalDayMinutes) * 100;
    const freeBlock = document.createElement('div');
    freeBlock.className = 'timeline-block free';
    freeBlock.style.width = `${remainingPct}%`;
    freeBlock.title = `Free: ${minutesToTime(currentPointer)} - 17:00`;
    elements.timelineTrack.appendChild(freeBlock);
  }
}

// ============================================================================
// UI Helpers & Utilities
// ============================================================================

function updateResultsHeader(count, day, start, end, isNow = false) {
  const dayName = formatDayName(day);
  const nowBadge = isNow ? ' (Live Now)' : '';
  elements.resultsSubtext.textContent = `Found ${count} available room${count === 1 ? '' : 's'} on ${dayName}, ${start} to ${end}${nowBadge}`;
}

function updateActiveFilterCount() {
  let count = 0;
  if (elements.filterBuilding.value) count++;
  if (elements.filterType.value) count++;
  if (elements.filterFloor.value) count++;
  if (elements.filterCapacity.value) count++;

  state.activeFiltersCount = count;
  if (count > 0) {
    elements.activeFilterBadge.textContent = `${count} active`;
    elements.activeFilterBadge.classList.remove('hidden');
  } else {
    elements.activeFilterBadge.classList.add('hidden');
  }
}

function resetFilters() {
  elements.filterBuilding.value = '';
  elements.filterType.value = '';
  elements.filterFloor.value = '';
  elements.filterCapacity.value = '';
  updateActiveFilterCount();
}

function syncPeriodChips() {
  const currentStart = elements.startTime.value;
  const currentEnd = elements.endTime.value;

  elements.periodChips.forEach(chip => {
    if (chip.dataset.start === currentStart && chip.dataset.end === currentEnd) {
      chip.classList.add('active-chip');
    } else {
      chip.classList.remove('active-chip');
    }
  });
}

function showLoading(isLoading) {
  if (isLoading) {
    elements.loadingState.classList.remove('hidden');
    elements.roomsGrid.classList.add('hidden');
    elements.emptyState.classList.add('hidden');
  } else {
    elements.loadingState.classList.add('hidden');
    elements.roomsGrid.classList.remove('hidden');
  }
}

function showAlert(title, message) {
  elements.alertTitle.textContent = title;
  elements.alertMessage.textContent = message;
  elements.alertBanner.classList.remove('hidden');
}

function hideAlert() {
  elements.alertBanner.classList.add('hidden');
}

function handleFetchError(error) {
  setServerStatus('disconnected', 'API Offline (localhost:8080)');
  showAlert(
    'Connection Error',
    `Could not fetch data from the server. Ensure the Spring Boot backend is running on http://localhost:8080. Details: ${error.message}`
  );
  elements.roomsGrid.innerHTML = '';
  elements.emptyState.classList.remove('hidden');
}

function formatRoomType(type) {
  switch (type) {
    case 'CLASSROOM': return 'Classroom';
    case 'LAB': return 'Lab';
    case 'SEMINAR_HALL': return 'Seminar Hall';
    default: return type || 'Room';
  }
}

function formatDayName(day) {
  const map = {
    'MON': 'Monday',
    'TUE': 'Tuesday',
    'WED': 'Wednesday',
    'THU': 'Thursday',
    'FRI': 'Friday',
    'SAT': 'Saturday'
  };
  return map[day] || day;
}

function mapDayNameToEnum(dayName) {
  if (!dayName) return 'MON';
  const clean = dayName.toUpperCase();
  if (clean.startsWith('MON')) return 'MON';
  if (clean.startsWith('TUE')) return 'TUE';
  if (clean.startsWith('WED')) return 'WED';
  if (clean.startsWith('THU')) return 'THU';
  if (clean.startsWith('FRI')) return 'FRI';
  if (clean.startsWith('SAT')) return 'SAT';
  return 'MON';
}

function formatDuration(minutes) {
  if (minutes < 60) {
    return `${minutes}m free`;
  }
  const hours = Math.floor(minutes / 60);
  const remaining = minutes % 60;
  return remaining > 0 ? `${hours}h ${remaining}m free` : `${hours}h free`;
}

function timeToMinutes(timeStr) {
  const [h, m] = timeStr.split(':').map(Number);
  return h * 60 + m;
}

function minutesToTime(totalMin) {
  const h = Math.floor(totalMin / 60).toString().padStart(2, '0');
  const m = (totalMin % 60).toString().padStart(2, '0');
  return `${h}:${m}`;
}

function calculateDurationMinutes(start, end) {
  return timeToMinutes(end) - timeToMinutes(start);
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}
