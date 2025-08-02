document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("adminRegisterForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
      const res = await fetch("/api/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          email,
          password,
          role: "ADMIN"
        })
      });

      if (!res.ok) {
        const msg = await res.text();
        document.getElementById("error").innerText = msg || "Registration failed.";
        return;
      }

      alert("Admin registered successfully.");
      window.location.href = "admin-login.html";
    } catch (err) {
      console.error("Admin registration error:", err);
      document.getElementById("error").innerText = "Something went wrong.";
    }
  });
});
