import { useState } from "react";
import "../styles/EditPage.css";

const EditPage = () => {
  const [activeTab, setActiveTab] = useState<"accommodation" | "activity">(
    "accommodation"
  );

  return (
    <div className="edit-page-container">
      <header className="header">
        <h1>TripSync</h1>
        <div className="user-icon">👤</div>
      </header>

      <div className="tabs">
        <button
          className={`tab-button ${
            activeTab === "accommodation" ? "active" : ""
          }`}
          onClick={() => setActiveTab("accommodation")}
        >
          Accommodation
        </button>
        <button
          className={`tab-button ${activeTab === "activity" ? "active" : ""}`}
          onClick={() => setActiveTab("activity")}
        >
          Activity
        </button>
      </div>

      <div className="form-container">
        {activeTab === "accommodation" && (
          <form className="form">
            <h2 className="form-title">Edit Accommodation</h2>
            <label>
              Name:
              <input type="text" placeholder="Enter accommodation name" />
            </label>
            <label>
              Description:
              <textarea placeholder="Enter description" />
            </label>
            <label>
              Destination:
              <input type="text" placeholder="Enter destination" />
            </label>
            <label>
              Start Date:
              <input type="date" />
            </label>
            <label>
              End Date:
              <input type="date" />
            </label>
            <div className="form-buttons">
              <button type="button" className="cancel-button">
                Cancel
              </button>
              <button type="submit" className="save-button">
                Save
              </button>
            </div>
          </form>
        )}

        {activeTab === "activity" && (
          <form className="form">
            <h2 className="form-title">Edit Activity</h2>
            <label>
              Name:
              <input type="text" placeholder="Enter activity name" />
            </label>
            <label>
              Description:
              <textarea placeholder="Enter description" />
            </label>
            <label>
              Location:
              <input type="text" placeholder="Enter location" />
            </label>
            <label>
              Date:
              <input type="date" />
            </label>
            <label>
              Time:
              <input type="time" />
            </label>
            <div className="form-buttons">
              <button type="button" className="cancel-button">
                Cancel
              </button>
              <button type="submit" className="save-button">
                Save
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default EditPage;
