import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignUpPage from './pages/SignUpPage';
import LoginPage from './pages/Loginpage';
import HomePage from './pages/HomePage';
import ProfilePage from './pages/ProfilePage';
import CreateTrip from './pages/CreateTrip';
import ActivityAccommodation from './pages/ActivityAccommodation';
import DetailTrip from './pages/DetailTrip'
import EditTrip from './pages/EditTrip';
import EditActivity from './pages/EditActivity';
import NotFoundPage from './pages/NotFoundPage';
import ProtectedRoute from './components/ProtectedRoute';
import PublicOnlyRoute from './components/PublicOnlyRoute';


function App() {

  return (
    <Router>
      <div className = "App">
        <Routes>
          {/* Logged-in users are sent to /home */}
          <Route element={<PublicOnlyRoute />}>
            <Route path = "/" element={<LoginPage />} />
            <Route path = "/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignUpPage />} />
          </Route>
          {/* Pages below require a logged-in user */}
          <Route element={<ProtectedRoute />}>
            <Route path="/home" element={<HomePage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/activity/:tripId" element={<ActivityAccommodation />} />
            <Route path = "/create-trip" element={<CreateTrip />} />
            <Route path = "/edit-trip/:id" element={<EditTrip />} />
            <Route path="/trip/:id" element={<DetailTrip />} />
            <Route path="/trip/:tripId/edit-items" element={<EditActivity />} />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
