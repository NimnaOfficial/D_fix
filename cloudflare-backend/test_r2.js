const AWS = require('aws-sdk');
const s3 = new AWS.S3({
    endpoint: 'https://bef78e3fe984f9643c696429b6b5d092.r2.cloudflarestorage.com',
    accessKeyId: 'fd4a08171f3c436d5ad978e3f0eb44fa',
    secretAccessKey: '35ee2938189168a913be6fb5d278ff65170722963be0b2bd2a075bda0d182476',
    signatureVersion: 'v4',
    region: 'auto'
});

s3.listBuckets((err, data) => {
    if (err) console.log("Error:", err.message);
    else console.log("Buckets:", data.Buckets);
});
