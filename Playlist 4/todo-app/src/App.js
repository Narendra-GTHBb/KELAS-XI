import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import TodoList from "./components/TodoList";
// Use the cleaned WriteForm component (keeps original Write.js untouched)
import Write from "./components/WriteForm";
import Read from "./components/Read";
import UpdateRead from "./components/UpdateRead";
import Update from "./components/Update";
import { useState } from "react";

function App() {
  const [selectedForUpdate, setSelectedForUpdate] = useState(null);

  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Root route for now shows Write component per Fasa 3 requirement */}
          <Route path="/" element={<Write />} />

          {/* Explicit write route */}
          <Route path="/write" element={<Write />} />

          {/* Keep existing TodoList available at /todos in case you need it */}
          <Route path="/todos" element={<TodoList />} />
          {/* Read route to fetch and display data from Firebase */}
          <Route path="/read" element={<Read />} />

          {/* UpdateRead receives setter to pass selected item to Update */}
          <Route
            path="/update-read"
            element={<UpdateRead setSelectedForUpdate={setSelectedForUpdate} />}
          />

          {/* Update route uses selectedForUpdate from parent state */}
          <Route
            path="/update"
            element={
              <Update
                selectedItem={selectedForUpdate}
                setSelectedItem={setSelectedForUpdate}
              />
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
