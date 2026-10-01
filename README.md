# 🏛️ HỆ THỐNG QUẢN LÝ THƯ VIỆN HƯỚNG ĐỐI TƯỢNG (OOP LIBRARY MANAGEMENT SYSTEM)

Dự án đồ án môn học Lập trình Hướng đối tượng (OOP) bằng **Java 21**, hiện thực hóa 100% theo bản thiết kế hướng đối tượng chuẩn từ bài viết [Thiết kế Hướng Đối Tượng Hệ Thống Quản Lý Thư Viện - Viblo](https://viblo.asia/p/thiet-ke-huong-doi-tuong-he-thong-quan-ly-thu-vien-5pPLkRZyJRZ).

---

## 🌟 TÍNH NĂNG NỔI BẬT

- **📚 Quản lý Danh mục Sách & Bản sao (Book & BookItem - R2, R3, R4, R14):**
  - Quản lý thông tin sách (ISBN, Tựa đề, Tác giả, Thể loại, NXB, Ngày XB, Ngôn ngữ, Số trang).
  - Quản lý từng bản sao vật lý cụ thể kèm mã vạch (Barcode), giá bán, định dạng (`BookFormat`), vị trí kệ sách (`Rack`), trạng thái (`BookStatus`).
  - Tìm kiếm đa năng theo Tựa đề, Tác giả, Thể loại, Ngày xuất bản, Mã vạch.
- **👥 Phân quyền Người dùng & Thẻ Thư viện (User, Librarian, Member, LibraryCard - R5, R6):**
  - Quản lý tài khoản thủ thư và thành viên.
  - Tự động cấp thẻ thư viện (`LibraryCard`) kèm mã thẻ duy nhất khi đăng ký thành viên mới.
  - Tính năng khóa/mở khóa tài khoản (Blacklist/Active).
- **⚡ Quy trình Mượn Sách (Checkout Book - R7, R8):**
  - Kiểm tra giới hạn mượn tối đa 10 cuốn sách / thành viên (R7).
  - Tự động ấn định thời hạn mượn 15 ngày (R8).
  - Kiểm tra điều kiện thẻ hợp lệ, không có khoản phạt nợ chưa đóng, không phải sách chỉ đọc tại chỗ.
- **📥 Quy trình Trả Sách & Tính Phạt Tự Động (Return Book & Fine - R1, R15):**
  - Tiếp nhận hoàn trả sách, tự động tính số ngày quá hạn và tiền phạt (5.000 VNĐ/ngày).
  - Tự động kích hoạt thông báo và quyền ưu tiên cho thành viên đang xếp hàng đặt trước.
- **⏳ Gia Hạn Mượn Sách (Renew Book - R11):**
  - Cho phép gia hạn thêm 15 ngày nếu sách chưa bị người khác đặt trước.
- **📑 Hàng Đợi Đặt Giữ Sách (Book Reservation - R9, R10, R13):**
  - Đặt giữ sách khi tất cả bản sao đều đang bận.
  - Mỗi bản sao chỉ cho phép 1 người đặt trước tại một thời điểm (R9).
  - Tự động chuyển trạng thái sang `PENDING` khi sách được trả về và gửi thông báo mời nhận sách.
- **🔔 Trung Tâm Thông Báo Tự Động (Notifications - R12):**
  - Hỗ trợ thông báo qua Email (`EmailNotification`) và Bưu điện (`PostalNotification`).
  - Tự động gửi thông báo nhắc nhở khi sách bị quá hạn hoặc khi sách đặt trước có sẵn.
- **💰 Quản Lý Tiền Phạt (Pay Fine - R15):**
  - Quản lý danh sách biên lai phạt, theo dõi trạng thái đã thanh toán / chưa thanh toán và thực hiện thu phí.
- **💾 Lưu Trữ Dữ Liệu Tự Động (Data Persistence):**
  - Tự động lưu và nạp trạng thái thư viện từ tệp dữ liệu `data/library_state.dat`.
- **💻 Đa Dạng Giao Diện:**
  - Hỗ trợ cả **Giao diện Đồ họa Hiện đại (Swing GUI)** và **Giao diện Console (CLI)**.

---

## 🚀 HƯỚNG DẪN CHẠY CHƯƠNG TRÌNH

### 1. Biên dịch dự án (1-Click Build)
Chạy tệp `build.bat` hoặc lệnh:
```bash
.\build.bat
```

### 2. Khởi chạy Giao diện Đồ họa (GUI)
Chạy tệp `run.bat` hoặc lệnh:
```bash
.\run.bat
```

### 3. Khởi chạy Giao diện Dòng lệnh (CLI)
Chạy tệp `run_cli.bat` hoặc lệnh:
```bash
.\run_cli.bat
```

---

## 📂 CẤU TRÚC THƯ MỤC DỰ ÁN

```
c:\DuAnMonHocCntt\DuANOpp\
├── src\
│   └── com\library\
│       ├── Main.java                        # Điểm khởi chạy ứng dụng
│       ├── enums\                           # Enum trạng thái, định dạng sách
│       ├── models\                          # Các thực thể dữ liệu (Book, BookItem, Rack, Card, Lending, Fine...)
│       ├── users\                           # Phân cấp người dùng (User, Member, Librarian)
│       ├── search\                          # Giao diện Search & Triển khai Catalog
│       ├── notifications\                   # Phân cấp thông báo (Notification, Email, Postal)
│       ├── services\                        # Tầng nghiệp vụ điều phối (Library, DataStorage, SampleData)
│       ├── utils\                           # Hằng số (Constants), Tiện ích ngày tháng, Bộ sinh mã
│       ├── gui\                             # Giao diện đồ họa hiện đại Swing (Panels, Theme, Dialogs)
│       └── cli\                             # Giao diện tương tác Console Terminal
├── data\                                    # Nơi lưu trữ trạng thái dữ liệu
├── docs\
│   └── BAO_CAO_DO_AN.md                     # Toàn bộ báo cáo đồ án chi tiết & Sơ đồ Mermaid UML
├── build.bat                                # Script biên dịch tự động
├── run.bat                                  # Script chạy giao diện GUI
├── run_cli.bat                              # Script chạy giao diện CLI
└── README.md                                # Tài liệu hướng dẫn
```

---

## 📖 BÁO CÁO ĐỒ ÁN CHI TIẾT
Vui lòng xem tài liệu chi tiết tại [docs/BAO_CAO_DO_AN.md](file:///c:/DuAnMonHocCntt/DuANOpp/docs/BAO_CAO_DO_AN.md) bao gồm:
- Phân tích chi tiết 15 yêu cầu nghiệp vụ (R1 - R15).
- Phân tích 4 tính chất OOP (Đóng gói, Kế thừa, Đa hình, Trừu tượng).
- Biểu đồ UML (Class Diagram, Use Case Diagram, Sequence Diagrams).
- Kịch bản kiểm thử (Test Cases) và hướng dẫn thuyết trình đồ án.
