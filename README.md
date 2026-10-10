# Alif Water Sort Pro 🧪💧

Premium Liquid Sorting Puzzle Game for Android built with Jetpack Compose & Material 3.

## 📦 Download Release Files (আলাদা আলাদা ডাউনলোড)

### অপশন ১: GitHub Actions থেকে ডাউনলোড (Direct Artifacts)
GitHub রেপোজিটরিতে যাওয়ার পর:
1. উপরে **Actions** ট্যাবে ক্লিক করুন।
2. সর্বশেষ **Build Release AAB & APK** ওয়ার্কফ্লোটিতে ক্লিক করুন।
3. পেইজের নিচের দিকে **Artifacts** সেকশনে ৩টি আলাদা আলাদা ডাউনলোড বাটন পাবেন:
   - 📥 **`1-GooglePlay-AAB-Release`**: গুগল প্লে কনসোলে আপলোড করার জন্য অফিশিয়াল রিলিজ `.aab` ফাইল (Release Signed)।
   - 📥 **`2-DirectInstall-APK-Release`**: সরাসরি অ্যান্ড্রয়েড ফোনে ইনস্টল এবং টেস্ট করার জন্য `.apk` ফাইল।
   - 📥 **`3-All-Files-APK-and-AAB`**: দুটি ফাইল একসাথে জিপ আকারে ডাউনলোড করার জন্য।

### অপশন ২: প্রজেক্ট ফোল্ডার থেকে সরাসরি ডাউনলোড
প্রজেক্টের `build_outputs/` ফোল্ডারে ফাইল দুটি তৈরি রয়েছে:
- `build_outputs/Alif_Water_Sort_Pro.aab` (Google Play Console Release)
- `build_outputs/Alif_Water_Sort_Pro.apk` (Android Phone Installable)

---

## 🔑 Release Signing Key Information
- **Keystore File:** `my-upload-key.jks` (encoded in `my-upload-key.jks.base64`)
- **Key Alias:** `upload`
- **Store & Key Password:** `upload1234`
- **Certificate Owner:** `CN=Alif Water Sort, OU=Games, O=Alif, L=Dhaka, ST=Dhaka, C=BD`
- **Validity:** 10,000 days (Year 2054 পর্যন্ত বৈধ)
