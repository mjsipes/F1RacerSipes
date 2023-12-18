let socket;

// Game state
let prompt = document.getElementById("prompt");
let gameStart = false;
let gameStop = false;

// Player state
let numErrors = 0;
let isError = false;
let isWinner = false;
let timeElapsed = 0;

// Player stats
let response = "";
let accuracy = 0;
let percentComplete = 0;
let numCharactersTyped = 0;
let numWordsTyped = 0;
let CPM = 0;
let WPM = 0;

let startTime;
let interval;

// Initialize typing input

function connectToServer() {
    socket = new WebSocket("ws://localhost:9090/f1racer2/GamingPageWebSocket");
    socket.onopen = function(event) {}; // connection opened
    socket.onmessage = handleSocketMessage;
    socket.onclose = function(event) {
        document.getElementById("response").innerHTML += "Disconnected!<br />";
    };
}

function handleSocketMessage(event) {
    console.log("Recieving: " + event.data);
    const msg = JSON.parse(event.data);

    switch (msg.type) {
        case "carPosition":
            updateCarPosition(msg.value);
            updateCarPositionOmar(msq.value);
            break;
        case "gameStop":
			gameStop = msg.value;
			updateIsWinnerOrLooser();
            break;
       	case "gameStart":
			gameStart = msg.value
            break;
        case "gameServerName":
            document.getElementById('gameServerName').innerText = msg.value;
            break;
        case "userName":
            document.getElementById('userName').innerText = msg.value;
            break;
        case "prompt":
            document.getElementById('prompt').innerText = msg.value;
            prompt = msg.value;
            break;
        default:
            console.error('Unknown message type:', msg.type);
    }
}
//the following 7 lines of code were provided by chatpt
function updateCarPosition(value) {
    const data = JSON.parse(value);
    let table = `<table border="1"><tr><th>Player Name</th><th>Game Status</th></tr>`;

    data.forEach(item => {
        table += `<tr><td>${item.serverName}</td><td>${item.gameStatus}</td></tr>`;
    });

    table += '</table>';
    document.getElementById('carPosition').innerHTML = table;
}




//the following 6 lines of code were provided by chatpt
function updateCarPositionOmar(value) {
	const data = JSON.parse(value);
	let table = `<table border="1"><tr><th>Player Name</th><th>Game Status (Percentage)</th></tr>`;
	let sprites = '';

	const defaultTranslate = "transform: translate(0px, -120px) scaleX(-1);";
	const carStyle = "width: 40px; height: auto; margin: auto; position: absolute;";

	if (!gameStart) {
		document.getElementById('RacingWindow').innerHTML = ''; // Clear existing sprites
	}

	data.forEach((item, index) => {
		table += `<tr><td>${item.serverName}</td><td>${item.gameStatus}</td></tr>`;
		const carPercentage = parseInt(item.gameStatus, 10) || 0;
		carPositions[index] = carPercentage;
		//the following 4 lines of code were provided by chatpt
		if (!gameStart) {
			sprites += `<img id="carSprite${index}" class="car-sprite"
                        src="https://opengameart.org/sites/default/files/spr_estatecar_0.png" alt="car"
                        style="${carStyle} ${defaultTranslate}">`;
		} else {
			for (let i = 0; i < carPositions.length; i++) {
				updatePosition(carPositions[i], i);
			}
		}

	});

	if (!gameStart) {
		sprites += `<div class="grass-texture">
                        <img id="grassPhoto" class="grass-photo" src="grass.jpg" alt="grass">
                    </div>`;
		document.getElementById('RacingWindow').innerHTML = sprites;
	}

	document.getElementById('carPosition').innerHTML = table;
}
function updatePosition(percentage, carNumber) {
	percentage = Math.min(100, Math.max(0, percentage));
	const angle = (Math.PI / 2) - ((percentage / 100) * 2 * Math.PI);

	const offset = carNumber * 2;

	const vradius = 115 + offset;
	const hradius = 310 + offset;
	const left = hradius * Math.cos(angle);
	const up = vradius * Math.sin(angle);
	//the following 4 lines of code were provided by chatpt
	const carSprite = document.getElementById(`carSprite${carNumber}`);
    if (carSprite) {
        let carAngle = calculateOvalTangentAngle(angle, hradius, vradius);
        carSprite.style.transform = `translate(${left}px, ${-up}px) rotate(${carAngle}deg) scaleX(-1)`;
    }
}


function calculateOvalTangentAngle(angle, hradius, vradius) {
    const dx = -hradius * Math.sin(angle);
    const dy = vradius * Math.cos(angle);

    let tangentAngle = Math.atan2(dy, dx) * (180 / Math.PI);

    // Adjust the angle for CSS coordinate system and flip the car
    return -(tangentAngle) + 180; // Add 180 degrees to flip the car
}








document.addEventListener('DOMContentLoaded', () => {
	
	let typingInput = document.getElementById('typingInput');
    typingInput.addEventListener('input', handleTypingInput);
    
});

function handleTypingInput(event) {
    if (!startTime) {
        startTime = new Date();
        interval = setInterval(updateTimer, 1000);
    }

    response = event.target.value;
    document.getElementById('response').textContent = response;
    numCharactersTyped = response.length;
	document.getElementById('numCharactersTyped').textContent = numCharactersTyped;

    
    updateIsError();
    updateAccuracy();
    //updateNumCharactersTyped(response.length);
    updateCPM();
    updatePercentComplete();
    updateIsWinnerOrLooser();
}






function updateTimer() {
    timeElapsed = (new Date() - startTime) / 1000; // Time in seconds
    document.getElementById('timer').textContent = timeElapsed.toFixed(0);
    updateCPM();
}


function updateIsError() {
    isError = !prompt.startsWith(response);
    if (isError && response.length > 0) {
        numErrors++;
    }
    document.getElementById('isError').textContent = isError;
}



function updateAccuracy(){
	accuracy = 1 - (numErrors/numCharactersTyped);
	//document.getElementById('accuracy').textContent = accuracy;
}


function updateNumCharactersTyped(){
	numCharactersTyped = response.length;
	document.getElementById('numCharactersTyped').textContent = numCharactersTyped;
}

function updateCPM() {
    timeElapsed = ((new Date() - startTime) / 1000) / 60; // Time in minutes
    CPM = timeElapsed > 0 ? (numCharactersTyped / timeElapsed).toFixed(2) : 0;
    document.getElementById('CPM').textContent = CPM;
}

function updateWPM(){}


function updatePercentComplete() {
	if (!isError){
		percentComplete = ((response.length / prompt.length) * 100).toFixed(2);
        document.getElementById('percentComplete').textContent = percentComplete;
        send("percentComplete", percentComplete);
	}
}

function updateIsWinnerOrLooser() {
    console.log(gameStart, gameStop);
    if (gameStart && !gameStop) {
        console.log(percentComplete);
        // Parse percentComplete as an integer
        if (parseInt(percentComplete, 10) === 100) {
            console.log("winner!");
            document.getElementById('endOfGameMessage').textContent = "you win :)";
            isWinner = true;
            send("isWinner", isWinner);
        }
    } else if (gameStart && gameStop && !isWinner) {
        document.getElementById('endOfGameMessage').textContent = "you loose :(";
    }
}


function send(type, value) {
    const jsonMessage = JSON.stringify({ type, value });
    socket.send(jsonMessage);
    console.log("Sending: "+ jsonMessage);
    return false;
}

// focus on the stuff down here later

function sendGameStats() {
    // Prepare your game statistics data
    const stats = {
        numErrors: numErrors,
        isError: isError,
        isWinner: isWinner,
        timeElapsed: timeElapsed,
        accuracy: accuracy,
        percentComplete: percentComplete,
        numCharactersTyped: numCharactersTyped,
        CPM: CPM,
        WPM: WPM
    };

    // Send data to server
    if (socket && socket.readyState === WebSocket.OPEN) {
        send("gameStats", stats);
    }
}
//the following 2 lines of code were provided by chatpt
window.addEventListener("beforeunload", function (event) {
    sendGameStats();
});
