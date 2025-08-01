import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import Dashboard from './components/Dashboard';
import MessageList from './components/MessageList';
import DeviceView from './components/DeviceView';
import JoinEventList from './components/JoinEventList';
import Header from './components/Header';
import Sidebar from './components/Sidebar';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <div className="app-body">
          <Sidebar />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/messages" element={<MessageList />} />
              <Route path="/devices/:deviceId" element={<DeviceView />} />
              <Route path="/join-events" element={<JoinEventList />} />
            </Routes>
          </main>
        </div>
      </div>
    </Router>
  );
}

export default App;