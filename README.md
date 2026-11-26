# HỆ THỐNG QUẢN LÝ THƯ VIỆN (ĐỒ ÁN LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG)

Ứng dụng Console viết bằng **Java** (JDK 11 trở lên). Thiết kế lớp tham khảo bài [Thiết kế Hướng Đối Tượng Hệ Thống Quản Lý Thư Viện - Viblo](https://viblo.asia/p/thiet-ke-huong-doi-tuong-he-thong-quan-ly-thu-vien-5pPLkRZyJRZ).

Dữ liệu được lưu trong **các lớp mảng đối tượng** (`DanhSachSach`, `DanhSachThanhVien`…, mảng thuần, không dùng `ArrayList`) và được đọc/ghi bằng **file text** trong thư mục `data/`.

## Chạy chương trình

- Windows: nhấp đúp **`run.bat`** (tự biên dịch lại rồi chạy), hoặc chạy `.\run.ps1` trong PowerShell.
- Chỉ biên dịch: `build.bat`.

Bản tải từ GitHub có sẵn 7 file dữ liệu demo trong `data/`. Chương trình đọc các file này khi khởi động và ghi vào đó sau khi thay đổi dữ liệu. Nếu chưa có `data/sach.txt`, chương trình tạo bộ mẫu mặc định. Menu **9 → 5** tạo lại bộ mẫu mặc định theo ngày chạy.

Tài khoản thủ thư mẫu: `LIB-101` / `admin123`. Thành viên mẫu: `MB-1001` → `MB-1004`.

## Chức năng

| Menu | Chức năng |
| :---: | :--- |
| 1 | Quản lý sách: xuất, tìm theo từ khóa / tựa đề / tác giả / thể loại / ngày xuất bản, thêm 1, nhập n, sửa, xóa, sắp xếp, thống kê |
| 2 | Mượn sách (tối đa 10 cuốn, hạn 15 ngày; chặn nếu thẻ bị khóa, còn nợ phạt, sách chỉ đọc tại chỗ hoặc đang được người khác đặt giữ) |
| 3 | Trả sách, tự tính tiền phạt trễ hạn (5.000đ/ngày), tự giữ sách cho người đã đặt |
| 4 | Gia hạn thêm 15 ngày |
| 5 | Đặt giữ / hủy đặt giữ sách đang được mượn |
| 6 | Thành viên: xuất, tìm, thêm (tự cấp mã và thẻ), sửa, xóa, lịch sử mượn, khóa/mở thẻ (thủ thư đăng nhập), xem tất cả người dùng |
| 7 | Tiền phạt: xuất, thống kê, thanh toán |
| 8 | Thông báo Email / Bưu điện, quét sách quá hạn để gửi nhắc nhở |
| 9 | Ghi / đọc lại file text, xem số bản ghi, thống kê tổng hợp, khôi phục dữ liệu mẫu |

## Cấu trúc thư mục

```
src/com/library/
├── Main.java
├── cli/            LibraryCLI - menu Console
├── collections/    IDanhSach (interface), DanhSachCoBan (abstract) + 7 lớp mảng đối tượng
├── models/         Book, BookItem, Author, Person, Address, Rack, LibraryCard, BookLending, BookReservation, Fine
├── users/          User (abstract), Member, Librarian
├── notifications/  Notification (abstract), EmailNotification, PostalNotification
├── search/         Search (interface)
├── services/       Library (nghiệp vụ), TextFileStorageService (file text), SampleDataLoader, OperationResult
├── utils/          Constants, DateUtils, IdGenerator (static), NhapLieu
└── enums/          BookStatus, BookFormat, AccountStatus, ReservationStatus
data/               sach.txt, thanhvien.txt, thuthu.txt, muontra.txt, datcho.txt, phat.txt, thongbao.txt (dữ liệu demo)
docs/BAO_CAO_DO_AN.md   Báo cáo: đối chiếu 10 tiêu chí, sơ đồ lớp, câu hỏi vấn đáp, kịch bản demo
```
