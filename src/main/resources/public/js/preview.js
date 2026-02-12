document.addEventListener('DOMContentLoaded', () => {
  const urls = document.querySelectorAll('.link');
  console.log(urls);

  for (const url of urls) {
    const id = url.getAttribute('data-id');
    const urlPreview = url.getAttribute('data-url');

    const data = { q: urlPreview };

    fetch('https://api.linkpreview.net', {
      method: 'POST',
      headers: {
        'X-Linkpreview-Api-Key': 'a1953d9b7a1283618650bcfb52dbd0bf',
      },
      mode: 'cors',
      body: JSON.stringify(data),
    })
      .then((res) => {
        if (!res.ok) {
          console.error(`Error: ${res.status} - ${res.statusText}`);
          if (res.status === 425) {
            console.error(
              'The server responded with a status of 425 (Too Early). Please try again later.',
            );
          }
          throw new Error('Failed to fetch link preview');
        }
        return res.json();
      })
      .then((response) => {
        const linkPreviewImage = document.querySelector(`.a${id}`);
        if (linkPreviewImage && response.image) {
          linkPreviewImage.src = response.image;
        } else {
          console.warn(`No image found for link with ID: ${id}`);
        }
      })
      .catch((error) => {
        console.error('Fetch error:', error);
      });
  }
});
