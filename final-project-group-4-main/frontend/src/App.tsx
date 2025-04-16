import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignUpPage from './pages/SignUpPage';
import LoginPage from './pages/Loginpage';
import HomePage from './pages/HomePage';
import ProfilePage from './pages/ProfilePage';
import CreateTrip from './pages/CreateTrip';
import ActivityAccommodation from './pages/ActivityAccommodation';
import DetailTrip from './pages/DetailTrip'
import EditTrip from './pages/EditTrip';


function App() {

  return (
    <Router>
      <div className = "App">
        <Routes>
          <Route path = "/" element={<LoginPage />} />
          <Route path = "/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/activity/:tripId" element={<ActivityAccommodation />} />
          <Route path = "/create-trip" element={<CreateTrip />} />
          <Route path = "/edit-trip/:id" element={<EditTrip />} />
          <Route path="/trip/:id" element={<DetailTrip />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
