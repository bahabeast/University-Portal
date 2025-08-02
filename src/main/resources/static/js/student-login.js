document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("studentLoginForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
      const res = await fetch("/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      if (!res.ok) {
        const text = await res.text();
        document.getElementById("error").innerText = text || "Login failed.";
        return;
      }

      const data = await res.json();
      if (data.role !== "STUDENT") {
        document.getElementById("error").innerText = "Unauthorized: not a student.";
        return;
      }

      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("studentId", data.studentId || "");

      window.location.href = "student-card.html";
    } catch (err) {
      console.error("Login error:", err);
      document.getElementById("error").innerText = "Something went wrong.";
    }
  });
});
