const calcDistance = (lat1, lon1, lat2, lon2) => {
    const R = 6371; 
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
              Math.sin(dLon/2) * Math.sin(dLon/2);
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
};

const branches = [
  {id: 'BR-001', name: 'TechFix Colombo', lat: 6.9271, lon: 79.8612},
  {id: 'BR-002', name: 'TechFix Galle', lat: 6.0329, lon: 80.2168},
  {id: 'BR-DAA176BF', name: 'TechFix-Kagalle', lat: 7.2475, lon: 80.34571}
];

// Test from Maharagama (Colombo suburb: ~6.85, 79.93)
const c_lat = 6.85;
const c_lon = 79.93;

let nearest = null;
let min = Infinity;
for (const b of branches) {
    const d = calcDistance(c_lat, c_lon, b.lat, b.lon);
    console.log(Distance to :  km);
    if (d < min) { min = d; nearest = b; }
}
console.log(Nearest is: );
