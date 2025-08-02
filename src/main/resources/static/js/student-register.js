document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("studentRegisterForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const fullName = document.getElementById("fullName").value.trim();
    const groupNumber = document.getElementById("groupNumber").value.trim();
    const course = parseInt(document.getElementById("course").value);
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const photo = document.getElementById("photo").files[0];

    const formData = new FormData();
    formData.append("data", new Blob([JSON.stringify({
      fullName,
      groupNumber,
      course,
      email,
      password,
      role: "STUDENT"
    })], { type: "application/json" }));

    if (photo) {
      formData.append("photo", photo);
    }

    try {
      const res = await fetch("/api/register-student", {
        method: "POST",
        body: formData
      });

      if (!res.ok) {
        const msg = await res.text();
        document.getElementById("error").innerText = msg || "Registration failed.";
        return;
      }

      alert("Student registered successfully.");
      window.location.href = "student-login.html";
    } catch (err) {
      console.error("Student registration error:", err);
      document.getElementById("error").innerText = "Something went wrong.";
    }
  });
});
