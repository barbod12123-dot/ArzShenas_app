برای استفاده از فونت فارسی وزیرمتن (دقیقاً مثل نسخه‌ی Flet):

1. فایل‌های زیر را از گیت‌هاب رسمی وزیرمتن دانلود کنید:
   https://github.com/rastikerdar/vazirmatn/releases
   (فایل‌های Vazirmatn-Regular.ttf ، Vazirmatn-Medium.ttf ، Vazirmatn-Bold.ttf)

2. نام فایل‌ها را به حروف کوچک و بدون خط تیره تغییر دهید (قوانین ریسورس اندروید):
   vazirmatn_regular.ttf
   vazirmatn_medium.ttf
   vazirmatn_bold.ttf

3. آن‌ها را داخل همین پوشه (app/src/main/res/font/) قرار دهید.

4. در فایل Theme.kt خط‌های کامنت‌شده‌ی مربوط به AppFontFamily را از حالت کامنت خارج کنید
   (دقیقاً مشخص شده با // TODO: FONT).

تا وقتی فونت اضافه نشده، برنامه با فونت پیش‌فرض سیستم اجرا و کامپایل می‌شود
(بدون خطا) — فقط ظاهر متن‌ها دقیقاً مثل نسخه‌ی وزیرمتن نخواهد بود.
