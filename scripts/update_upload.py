import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

target = r'''      // -- UPLOAD \(R2\) --.*?// -- SERVE IMAGE \(R2\) --'''

replacement = r'''      // -- UPLOAD (ImgBB) --
      if (path === "/api/upload" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user) return json({ success: false, message: "Unauthorized" }, 401);
        
        try {
            const formData = await request.formData();
            const file = formData.get("file");
            if (!file) return json({ success: false, message: "No file provided" }, 400);

            // Using ImgBB as free cloud storage to bypass disabled R2 buckets
            const imgbbData = new FormData();
            imgbbData.append("key", "4b5b7cb36b2255745129c54e0c1f2115");
            imgbbData.append("image", file);

            const imgResponse = await fetch("https://api.imgbb.com/1/upload", {
                method: "POST",
                body: imgbbData
            });
            
            const imgbbResult = await imgResponse.json();
            
            if (imgbbResult.success && imgbbResult.data && imgbbResult.data.url) {
                return json({ success: true, url: imgbbResult.data.url });
            } else {
                return json({ success: false, message: "Image upload failed" }, 500);
            }
        } catch(err) {
            return json({ success: false, message: err.message }, 500);
        }
      }

      // -- SERVE IMAGE (R2) --'''

if re.search(target, content, re.DOTALL):
    content = re.sub(target, replacement, content, flags=re.DOTALL)
    with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Success replacing R2 upload with ImgBB")
else:
    print("Failed to find R2 upload target")
