function copyToClipboard(button) {
  const url = button.getAttribute("data-url");
  const fullUrl = `${window.location.origin}/s/${url}`;

  navigator.clipboard
    .writeText(fullUrl)
    .then(() => {
      alert(`Link copied to clipboard: ${fullUrl}`);
    })
    .catch((err) => {
      console.error("Error copying text: ", err);
    });
}
