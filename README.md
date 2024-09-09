# F1Racer: A Typing Game

![ScreenRecording2024-08-04at2 59 48PM-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/9f65c955-7829-4bdb-b113-a8e5691ece36)

F1Racer is an evolution of the classic racing game, where players not only race with random prompts but they can also create their own.

F1Racer is currently in development. Here is a demo of the current progress.

https://vimeo.com/895960265?share=copy

My current goals for the project: impliment a GUI and cool html elements for the game playing page. Add a game state for "waiting to start." Will countdown for 1 minute until starting game, or until creator of game overrides and clicks start game now. Add chatroom on the side of the game where players can send messages of emojis to the other players before and after the game ends.

### Background
During the fall semester of my junior year, I took a software development class where I learned Java, multithreading, concurrency, HTML, CSS, and SQL. In the second half of the semester, I was part of a team tasked with building a web application that demonstrated the skills we were learning in class. One lecture introduced WebSockets, and our instructor demonstrated a simple server/client chat room. I was fascinated by the concept and realized this server-client architecture could be used to build a multiplayer game, where instead of chat rooms, there are game servers, and instead of chat clients, there are players.

I convinced my team to create such a multiplayer game for our group project. We decided on a typing game inspired by Typeracer and called it F1Racer.

The project was challenging and took a lot of effort. I worked from 5 PM to 5 AM every day during Thanksgiving break to complete it. It was my favorite class project, and I learned so much from the experience.

### What I Learned

I discovered that every time you click a button on a web page, an HTTP request is sent to a backend server that processes the request.
I learned that when a page is redirected, the server doesn’t automatically know it's the same user, which is why information must be stored in the browser, like a cookie or session ID.
I implemented WebSockets for asynchronous communication, which was really exciting once I got it to work.
I used JavaScript for the first time to code the game-state logic, which made me realize how powerful and flexible JavaScript can be.
It was also my first experience using an SQL database, which I used to store information about players and games.
I coded this project with javaservlets on the backend. This was actually a requirement. 

Now I use this project to help learn new techonlogies. After my junior year over the summer, I wanted to host this project on the web for others to use, and I wanted to rebuild it with a modern framework. So as it exists now, the project is running with a react front end, a supabase backend, and it is hosted on vercel. 

Nodejs
React
Supabase
Vercel


I plan to continue to use this project to learn more about coding. I want to add more advanced gui elements, add ai/llm bots that you can play against and create more funcitonality.

This project has been very special and important to my learning journey as a computer science major and asipring software developer.
