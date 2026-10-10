# 📱 Alif Water Sort Pro - পাবলিশিং এবং ডাউনলোড গাইড

এই ডকুমেন্টে দেখানো হয়েছে কীভাবে আপনি আপনার গেমটির **APK** এবং **AAB (Android App Bundle)** ডাউনলোড করে **Google Play Store** এবং **Amazon Appstore**-এ প্রকাশ করবেন।

---

## 🚀 ১. সরাসরি এই প্রজেক্ট থেকে ডাউনলোড করার উপায় (AI Studio Settings)

AI Studio ইন্টারফেসের উপরে ডানদিকের **সেটিংস / মেনু (⋮ বা Settings)** বাটনে ক্লিক করুন:
1. **Export to GitHub:** আপনার GitHub অ্যাকাউন্টে এক ক্লিকে কোড পুশ করুন।
2. **Download / Export as ZIP:** সম্পূর্ণ প্রজেক্টটি ডাউনলোড করুন। প্রজেক্টের ভেতরে `build_outputs/` ফোল্ডারে ইতোমধ্যে তৈরি করা ফাইলগুলো রয়েছে:
   - `build_outputs/Alif_Water_Sort_Pro.apk`
   - `build_outputs/Alif_Water_Sort_Pro.aab`
3. **Generate APK / AAB:** মেনুতে "Build APK" বা "Generate AAB" অপশন থেকেও সরাসরি ডাউনলোড করতে পারবেন।

---

## 🐙 ২. GitHub Actions থেকে অটোমেটিক ডাউনলোড করার সিস্টেম (সক্রিয় করা হয়েছে)

আপনার প্রজেক্টে `.github/workflows/build_apk_aab.yml` স্বয়ংক্রিয় CI/CD যুক্ত করা হয়েছে:
1. আপনি যখনই গেমটি আপনার **GitHub** রিপোজিটরিতে পুশ করবেন, GitHub Actions স্বয়ংক্রিয়ভাবে কোডটি বিল্ড করবে।
2. আপনার GitHub রিপোজিটরির **"Actions"** ট্যাবে যান।
3. লেটেস্ট বিল্ডে ক্লিক করলেই নিচে **"Artifacts"** সেকশনে পাবেন:
   - 📦 **Alif-Water-Sort-Pro-Binaries** (যার ভেতরে `.apk` এবং `.aab` থাকবে)।
4. জাস্ট ১-ক্লিকে ডাউনলোড করে নিন!

---

## 🛒 ৩. Google Play Store-এ পাবলিশ করার নিয়ম

Google Play Store-এ অ্যাপ সাবমিট করতে **AAB (Android App Bundle)** ফাইল প্রয়োজন হয়:
1. **Google Play Console**-এ লগইন করুন (`play.google.com/console`)।
2. **Create App** এ ক্লিক করে নাম দিন: `Alif Water Sort Pro`, ভাষা নির্বাচন করুন, এবং Free/Game সিলেক্ট করুন।
3. **Production** বা **Internal Testing** ট্যাবে গিয়ে **Create new release** এ ক্লিক করুন।
4. `Alif_Water_Sort_Pro.aab` ফাইলটি ড্র্যাগ করে আপলোড করুন।
5. স্টোর লিস্টিং (টাইটেল, ডেসক্রিপশন, স্ক্রিনশট ও আইকন) পূরণ করে **Submit for Review** চাপুন।

---

## 📦 ৪. Amazon Appstore-এ পাবলিশ করার নিয়ম

Amazon Appstore সরাসরি **APK** ফাইল গ্রহণ করে:
1. **Amazon Developer Portal**-এ যান (`developer.amazon.com`)।
2. **Add a New App** -> **Android** নির্বাচন করুন।
3. টাইটেল ও ক্যাটাগরি দিন: `Games` -> `Brain & Puzzle`।
4. **APK Files** ট্যাবে গিয়ে `Alif_Water_Sort_Pro.apk` ফাইলটি আপলোড করুন।
5. স্ক্রিনশট ও প্রাইসিং সেট করে **Submit App** এ ক্লিক করুন।

---

## 🛠️ ৫. নিজের কম্পিউটারে তৈরি করার কমান্ড (টার্মিনাল থেকে)

যদি আপনি প্রজেক্টটি আপনার কম্পিউটারে নিয়ে অ্যান্ড্রয়েড স্টুডিও বা কমান্ড লাইনে বিল্ড করতে চান:

```bash
# ১. Amazon Appstore এবং সরাসরি ফোনের জন্য APK তৈরি:
./gradlew :app:assembleDebug

# ২. Google Play Store-এর জন্য AAB বান্ডিল তৈরি:
./gradlew :app:bundleDebug
```
বিল্ড শেষ হলে ফাইলগুলো যথাক্রমে `app/build/outputs/apk/` এবং `app/build/outputs/bundle/` ফোল্ডারে পাবেন।
