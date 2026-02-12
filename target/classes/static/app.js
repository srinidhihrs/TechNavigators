const triggerBtn = document.getElementById('triggerBtn');
const nextStepBtn = document.getElementById('nextStepBtn');
const runAllBtn = document.getElementById('runAllBtn');
const logsDiv = document.getElementById('logs');
const activeCountEl = document.getElementById('active-agent-count');
const canvas = document.getElementById('agent-vis-canvas');
const ctx = canvas.getContext('2d');

let pollingInterval;
let particles = [];
let animationFrameId;

// Step Control State
let logQueue = [];
let isSimulationRunning = false;
let isAutoRunning = false;

// Resize canvas
function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
}
window.addEventListener('resize', resizeCanvas);
resizeCanvas();

// Mock Data for Demo (Enhanced Descriptions)
// Mock Data for Demo (Detailed Script)
const MOCK_LOGS = [
    "[SYSTEM] 🔄 Initializing Multi-Agent Orchestration Protocol...",
    "[RootManager] ⚡ TRIGGER: Check active PO status. Invoking InventoryAgent...",
    "[InventoryAgent] 🔍 Checking SAP for active orders and stock levels...",
    "[InventoryAgent] ℹ️ REPORT: Stock critical for Item #X99. New order required. Notifying Root.",
    "[RootManager] 📋 ACTION: Initiating procurement. Invoking ProcurementAgent...",
    "[ProcurementAgent] 🛒 SEARCH: Finding supplier for Item #X99...",
    "[ProcurementAgent] 📝 ORDER PLACED: PO #ORD-101 created with Supplier A. Notifying Root.",
    "[RootManager] 📣 NOTIFY: Inform stakeholders of new order. Invoking NotificationAgent...",
    "[NotificationAgent] 📧 ALERT: Supplier A and SAP notified of PO #ORD-101.",
    "[RootManager] 📡 MONITOR: Track shipment. Invoking LogisticsAgent...",
    "[LogisticsAgent] 🚚 TRACKING: Monitoring PO #ORD-101 with Supplier A...",
    "[LogisticsAgent] ⚠️ DELAY DETECTED: Supplier A reporting 2-week delay. Escalating to Root.",
    "[RootManager] 🛑 ACTION: Cancel delayed order. Invoking ProcurementAgent...",
    "[ProcurementAgent] ❌ CANCEL: PO #ORD-101 cancelled. Searching alternate...",
    "[ProcurementAgent] ✅ RE-ORDER: PO #ORD-102 placed with Supplier B (Fast-Track). Notifying Root.",
    "[RootManager] 📣 UPDATE: Notify cancellation and new order. Invoking NotificationAgent...",
    "[NotificationAgent] 📧 ALERT: Supplier A (Cancel) & Supplier B (New Order) notified.",
    "[NotificationAgent] 📝 SAP UPDATE: PO #ORD-101 voided. PO #ORD-102 active.",
    "[RootManager] 📡 MONITOR: Track new shipment. Invoking LogisticsAgent...",
    "[LogisticsAgent] 🚚 TRACKING: Monitoring PO #ORD-102 with Supplier B. Status: On Time.",
    "[RootManager] 🏁 ORCHESTRATION COMPLETE: Supply chain stabilized."
];

// Agent Node Mapping
const agentNodes = {
    'root': document.getElementById('agent-root'),
    'logistics': document.getElementById('agent-logistics'),
    'inventory': document.getElementById('agent-inventory'),
    'procurement': document.getElementById('agent-procurement'),
    'notification': document.getElementById('agent-notification'),
    'system': document.getElementById('agent-root')
};

// --- Connection Drawing ---
// Support multiple parallel connections
let activeConnections = []; // Array of { from: 'root', to: 'logistics', progress: 0, id: uniqueId }

function drawArrow(fromRect, toRect, progress) {
    const startX = fromRect.left + fromRect.width / 2;
    const startY = fromRect.top + fromRect.height / 2;
    const endX = toRect.left + toRect.width / 2;
    const endY = toRect.top + toRect.height / 2;

    const currentX = startX + (endX - startX) * progress;
    const currentY = startY + (endY - startY) * progress;

    // Draw Line
    ctx.beginPath();
    ctx.moveTo(startX, startY);
    ctx.lineTo(currentX, currentY);
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.6)';
    ctx.lineWidth = 3;
    ctx.setLineDash([5, 5]);
    ctx.stroke();
    ctx.setLineDash([]);

    // Draw Arrowhead at tip
    if (progress > 0.1) {
        const angle = Math.atan2(endY - startY, endX - startX);
        const headLen = 12;
        ctx.beginPath();
        ctx.moveTo(currentX, currentY);
        ctx.lineTo(currentX - headLen * Math.cos(angle - Math.PI / 6), currentY - headLen * Math.sin(angle - Math.PI / 6));
        ctx.lineTo(currentX - headLen * Math.cos(angle + Math.PI / 6), currentY - headLen * Math.sin(angle + Math.PI / 6));
        ctx.fillStyle = '#fff';
        ctx.fill();
    }
}


// --- Event Listeners ---

triggerBtn.addEventListener('click', async () => {
    if (isSimulationRunning) return;

    startSimulationState();

    // In demo mode or real mode, we fill the queue first
    logQueue = [...MOCK_LOGS];

    // Enable Controls
    enableControls();

    addLogToUI("[SYSTEM] Ready. Click 'Next Step' or 'Run All' to proceed.");
});

nextStepBtn.addEventListener('click', () => {
    if (logQueue.length === 0) return;
    stepSimulation();
});

runAllBtn.addEventListener('click', () => {
    if (logQueue.length === 0 || isAutoRunning) return;
    isAutoRunning = true;
    runAllBtn.disabled = true;
    runAllBtn.style.opacity = '0.5';
    runAllBtn.innerHTML = '<span class="icon">⏩</span> <span class="btn-text">Running...</span>';

    // Disable Next Step during auto-run
    nextStepBtn.disabled = true;
    nextStepBtn.style.opacity = '0.5';
    nextStepBtn.style.cursor = 'not-allowed';

    autoRunStep();
});

function autoRunStep() {
    if (logQueue.length > 0) {
        stepSimulation();
        setTimeout(autoRunStep, 1500); // 1.5s delay between steps
    } else {
        isAutoRunning = false;
    }
}

function stepSimulation() {
    const log = logQueue.shift();
    processLogEntry(log);

    if (logQueue.length === 0) {
        completeSimulation();
    }
}

function startSimulationState() {
    isSimulationRunning = true;
    triggerBtn.classList.add('disabled');
    triggerBtn.innerHTML = '<span class="icon">⏳</span> <span class="btn-text">Initializing...</span>';
    logsDiv.innerHTML = '';
    logQueue = [];
    resetAgents();
}

function enableControls() {
    nextStepBtn.disabled = false;
    nextStepBtn.style.opacity = '1';
    nextStepBtn.style.cursor = 'pointer';

    runAllBtn.disabled = false;
    runAllBtn.style.opacity = '1';
    runAllBtn.style.cursor = 'pointer';

    triggerBtn.innerHTML = '<span class="icon">🟢</span> <span class="btn-text">Active</span>';
}

function completeSimulation() {
    isSimulationRunning = false;
    nextStepBtn.disabled = true;
    nextStepBtn.style.opacity = '0.5';
    nextStepBtn.style.cursor = 'not-allowed';

    runAllBtn.disabled = true;
    runAllBtn.style.opacity = '0.5';
    runAllBtn.style.cursor = 'not-allowed';
    runAllBtn.innerHTML = '<span class="icon">▶️</span> <span class="btn-text">Run All</span>';

    triggerBtn.classList.remove('disabled');
    triggerBtn.innerHTML = '<span class="icon">✅</span> <span class="btn-text">Restart Protocol</span>';

    // Clear arrows and particles on complete
    setTimeout(() => {
        resetAgents();
    }, 1000);
}

// Particle System (Flying Paper)
class Particle {
    constructor(startNode, endNode, color, label) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.color = color;
        this.speed = 0.012; // Slower for reading text
        this.progress = 0;
        this.icon = "📄";
        this.label = label || ""; // Info to carry
    }
    update() {
        this.progress += this.speed;

        const startRect = this.startNode.getBoundingClientRect();
        const endRect = this.endNode.getBoundingClientRect();

        const startX = startRect.left + startRect.width / 2;
        const startY = startRect.top + startRect.height / 2;
        const endX = endRect.left + endRect.width / 2;
        const endY = endRect.top + endRect.height / 2;

        this.currentX = startX + (endX - startX) * this.progress;
        this.currentY = startY + (endY - startY) * this.progress;

        return this.progress >= 1;
    }
    draw(ctx) {
        // Draw background circle
        ctx.beginPath();
        ctx.arc(this.currentX, this.currentY, 20, 0, Math.PI * 2);
        ctx.fillStyle = "rgba(30, 30, 35, 0.9)";
        ctx.fill();
        ctx.strokeStyle = this.color;
        ctx.lineWidth = 2;
        ctx.stroke();

        // Icon
        ctx.font = `24px Arial`;
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillStyle = "#ffffff";
        ctx.fillText(this.icon, this.currentX, this.currentY);

        // Label (Floating above)
        if (this.label) {
            ctx.font = "bold 12px 'Inter', sans-serif";
            ctx.fillStyle = "#ffffff";
            ctx.strokeStyle = "black";
            ctx.lineWidth = 3;
            ctx.strokeText(this.label, this.currentX, this.currentY - 30);
            ctx.fillText(this.label, this.currentX, this.currentY - 30);
        }
    }
}

// Animation Loop
function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Draw Active Connections
    activeConnections.forEach(conn => {
        const fromNode = agentNodes[conn.from];
        const toNode = agentNodes[conn.to];

        if (fromNode && toNode) {
            conn.progress = Math.min(conn.progress + 0.03, 1);
            drawArrow(fromNode.getBoundingClientRect(), toNode.getBoundingClientRect(), conn.progress);
        }
    });

    // Remove completed connections that are old? 
    // Actually, user wants "Parallel" flows, so we keep them until reset or manually cleared?
    // Let's keep them until resetAgents() is called at the end or start.

    // Particles
    for (let i = particles.length - 1; i >= 0; i--) {
        const p = particles[i];
        if (p.update()) particles.splice(i, 1);
        else p.draw(ctx);
    }

    requestAnimationFrame(animate);
}
animate();


function processLogEntry(text) {
    addLogToUI(text);
    visualizeInteraction(text);
}

function addLogToUI(text) {
    const div = document.createElement('div');
    div.classList.add('message-wrapper');

    const timestamp = new Date().toLocaleTimeString('en-US', { hour12: false });

    let type = 'system';
    let content = text;
    let agentName = 'System';

    if (text.startsWith('[SYSTEM]')) {
        type = 'system';
        content = text.replace('[SYSTEM]', '').trim();
    } else if (text.includes(']')) {
        const parts = text.split(']');
        if (parts.length > 1) {
            agentName = parts[0].replace('[', '').trim();
            content = parts[1].trim();

            if (agentName === 'RootManager') type = 'root';
            else if (agentName === 'LogisticsAgent') type = 'logistics';
            else if (agentName === 'InventoryAgent') type = 'inventory';
            else if (agentName === 'ProcurementAgent') type = 'procurement';
            else if (agentName === 'NotificationAgent') type = 'notification';
            else if (agentName === 'SAP') type = 'sap';
        }
    }

    div.classList.add(type);

    const contentDiv = document.createElement('div');
    contentDiv.classList.add('message-content');
    contentDiv.innerHTML = `<span class="timestamp">${agentName} • ${timestamp}</span><span class="text">${content}</span>`;

    div.appendChild(contentDiv);
    logsDiv.appendChild(div);
    logsDiv.scrollTop = logsDiv.scrollHeight;
}

function visualizeInteraction(text) {
    // STRICT SEQUENTIAL: Clear EVERYTHING before a new step
    document.querySelectorAll('.agent-node').forEach(el => el.classList.remove('active', 'working'));
    activeConnections = [];
    particles = [];

    let activeId = '';
    let color = '#fff';
    let label = "Info";

    // 1. Identify Actor
    if (text.includes('RootManager')) { activeId = 'root'; color = '#6366f1'; }
    else if (text.includes('LogisticsAgent')) { activeId = 'logistics'; color = '#f59e0b'; }
    else if (text.includes('InventoryAgent')) { activeId = 'inventory'; color = '#0ea5e9'; }
    else if (text.includes('ProcurementAgent')) { activeId = 'procurement'; color = '#ec4899'; }
    else if (text.includes('NotificationAgent')) { activeId = 'notification'; color = '#10b981'; }

    // 2. Extract Label (Short summary of message)
    if (text.includes(':')) {
        let rawContent = text.split(':').slice(1).join(':').trim();
        // Truncate
        label = rawContent.length > 25 ? rawContent.substring(0, 22) + "..." : rawContent;
    }

    if (activeId) {
        const node = agentNodes[activeId];
        node.classList.add('active', 'working');

        // 3. Routing Logic (Root Orchestrator Pattern)
        let targetId = null;

        if (activeId === 'root') {
            // Root Manager invoking specific agents
            if (text.includes('Inventory')) targetId = 'inventory';
            else if (text.includes('Procurement')) targetId = 'procurement';
            else if (text.includes('Notification')) targetId = 'notification';
            else if (text.includes('Logistics')) targetId = 'logistics';
        } else {
            // All other agents report back to Root Manager
            targetId = 'root';
        }

        // 4. Create Visuals
        if (targetId && targetId !== activeId && agentNodes[targetId]) {
            // Connection
            activeConnections.push({
                from: activeId,
                to: targetId,
                progress: 0,
                id: Date.now()
            });

            // Particle with label
            particles.push(new Particle(agentNodes[activeId], agentNodes[targetId], color, label));

            // Highlight target too?
            // agentNodes[targetId].classList.add('active'); // Optional: show target activating
        }
    }
}

function resetAgents() {
    document.querySelectorAll('.agent-node').forEach(el => el.classList.remove('active', 'working'));
    activeConnections = [];
    particles = [];
}
