const getAnalyticsData = async () => {
  const hash = document.body.dataset.hash ?? "";
  const endpoint = hash
    ? `/shortened/analytics_data/${hash}`
    : "/shortened/analytics_data";

  try {
    const response = await axios.get(endpoint);
    const analyticsData = response.data;

    const chartData = {
      labels: Object.keys(analyticsData),
      datasets: [
        {
          label: "Clicks per day and hour",
          data: Object.values(analyticsData),
          backgroundColor: getBackgroundColors(
            Object.keys(analyticsData).length,
          ),
          borderColor: getBorderColors(Object.keys(analyticsData).length),
          borderWidth: 1,
        },
      ],
    };

    const ctx = document.getElementById("myChart").getContext("2d");
    const config = {
      type: "bar",
      data: chartData,
      options: {
        scales: {
          y: {
            beginAtZero: true,
          },
        },
      },
    };
    new Chart(ctx, config);
  } catch (error) {
    console.error("Error at loading the data:", error);
  }
};

const getBackgroundColors = (numColors) => {
  const colors = [];
  for (let i = 0; i < numColors; i++) {
    const color = `rgba(${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, 0.6)`;
    colors.push(color);
  }
  return colors;
};
const getBorderColors = (numColors) => {
  const colors = [];
  for (let i = 0; i < numColors; i++) {
    const backgroundColor = `rgba(${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, 0.6)`;
    // Convertir el color de fondo a un color ligeramente más oscuro para el borde
    const borderColor = backgroundColor.replace(/[^,]+(?=\))/, "0.6)");
    colors.push(borderColor);
  }
  return colors;
};

document.addEventListener("DOMContentLoaded", getAnalyticsData);
