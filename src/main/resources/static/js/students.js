document.addEventListener("DOMContentLoaded", async () => {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (!token || role !== "ADMIN") {
    window.location.href = "admin-login.html";
    return;
  }

  const tableBody = document.getElementById("studentsTable");

  async function loadStudents() {
    try {
      const res = await fetch("/students", {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error("Failed to fetch students");

      const students = await res.json();
tableBody.innerHTML = students.map(student => `
  <tr>
    <td>${student.fullName}</td>
    <td>${student.groupNumber}</td>
    <td>${student.course}</td>
    <td>
      <div class="btn-group center">
        <button class="small-btn view-photo-btn" onclick="showPhoto(${student.id})">View</button>
      </div>
    </td>
    <td>
      <div class="btn-group center">
        <button class="small-btn" onclick="editStudent(${student.id}, '${student.fullName}', '${student.groupNumber}', ${student.course})">Edit</button>
        <button class="small-btn secondary" onclick="deleteStudent(${student.id})">Delete</button>
      </div>
    </td>
  </tr>
`).join("");
    } catch (err) {
      console.error("Error loading students:", err);
      alert("Failed to load students.");
    }
  }

window.showPhoto = function (id) {
  const imgWindow = window.open("", "Photo", "width=180,height=180");
  const img = new Image();
  img.src = `/students/photo/${id}`;
  img.style.maxWidth = "100%";
  img.style.height = "auto";
  imgWindow.document.body.style.margin = "0";
  imgWindow.document.body.appendChild(img);
};


  window.editStudent = function (id, name, group, course) {
    const newName = prompt("Edit Full Name:", name);
    const newGroup = prompt("Edit Group Number:", group);
    const newCourse = prompt("Edit Course:", course);

    if (!newName || !newGroup || isNaN(parseInt(newCourse))) {
      alert("Invalid input. Edit canceled.");
      return;
    }

    fetch(`/students/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({
        fullName: newName,
        groupNumber: newGroup,
        course: parseInt(newCourse)
      })
    })
    .then(res => {
      if (!res.ok) throw new Error("Update failed");
      loadStudents();
    })
    .catch(err => {
      console.error("Edit error:", err);
      alert("Failed to update student.");
    });
  };

  window.deleteStudent = async function (id) {
    if (!confirm("Are you sure you want to delete this student?")) return;

    try {
      const res = await fetch(`/students/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error("Deletion failed");
      alert("Student deleted");
      loadStudents();
    } catch (err) {
      console.error("Delete error:", err);
      alert("Failed to delete student.");
    }
  };

  loadStudents();
});
