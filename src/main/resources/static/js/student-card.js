document.addEventListener("DOMContentLoaded", async () => {
  const token = localStorage.getItem("token");
  const studentId = localStorage.getItem("studentId");

  if (!token || !studentId) {
    window.location.href = "student-login.html";
    return;
  }

  try {
    const res = await fetch(`/students/${studentId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });

    if (!res.ok) {
      localStorage.clear();
      window.location.href = "student-login.html";
      return;
    }

    const student = await res.json();

    document.getElementById("name").textContent = student.fullName;
    document.getElementById("group").textContent = student.groupNumber;
    document.getElementById("course").textContent = student.course;

    const photo = document.getElementById("photoImg");
    photo.src = `/students/photo/${student.id}`;
    photo.onerror = () => {
      photo.alt = "No photo available";
      photo.style.display = "none";
    };
  } catch (err) {
    console.error("Failed to load student:", err);
    window.location.href = "student-login.html";
  }
});

function logout() {
  localStorage.clear();
  window.location.href = "student-login.html";
}
