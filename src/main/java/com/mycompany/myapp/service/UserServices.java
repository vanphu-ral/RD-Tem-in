package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.service.dto.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

// ! chưa làm được
// ? Chưa test với front-end
// * Ngăn cách
// ☺ đã Test với front-end
@Service
public class UserServices {

    private final Logger log = LoggerFactory.getLogger(UserServices.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuanLyThongSoRepository quanLyThongSoRepository;

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private ThongSoMayRepository thongSoMayRepository;

    @Autowired
    private KichBanRepository kichBanRepository;

    @Autowired
    private ChiTietKichBanRepository chiTietKichBanRepository;

    @Autowired
    private SanXuatHangNgayRepository sanXuatHangNgayRepository;

    @Autowired
    private ChiTietSanXuatRepository chiTietSanXuatRepository;

    @Autowired
    private NhomThietBiRepository nhomThietBiRepository;

    @Autowired
    private DonViRepository donViRepository;

    @Autowired
    private DayChuyenRepository dayChuyenRepository;

    @Autowired
    private KichBanChangeStatusRepository kichBanChangeStatusRepository;

    @Autowired
    private LenhSanXuatRepository lenhSanXuatRepository;

    @Autowired
    private ChiTietLenhSanXuatRepository chiTietLenhSanXuatRepository;

    @Autowired
    private NhomSanPhamRepository nhomSanPhamRepository;

    @Autowired
    private scanDetailCheckRepository scanDetailCheckRepository;

    @Autowired
    private scanGroupMachinesRepository scanGroupMachinesRepository;

    @Autowired
    private scanLoginHistoryRepository scanLoginHistoryRepository;

    @Autowired
    private scanMachinesRepository scanMachinesRepository;

    @Autowired
    private scanProductRepository scanProductRepository;

    @Autowired
    private scanprofileCheckRepository scanprofileCheckRepository;

    @Autowired
    private scanWorkOrderRepository scanWorkOrderRepository;

    @Autowired
    private scanProductVersionRepository scanProductVersionRepository;

    //☺ Template login - Chức năng xác thực tài khoản
    //    public ResponseMessage loginAuth(UserPostRequest request) {
    //        UserEntity entity = userRepository.getByUserName(request.getUserName());
    //        boolean result = entity.getPassword().equals(request.getPassword());
    //        log.info("entity: " + entity);
    //        System.out.println("request: " + request);
    //        if (!result) {
    //            log.info("failed");
    //            return new ResponseMessage("Tài khoản hoặc mật khẩu bị sai");
    //        } else {
    //            log.info("success");
    //            // gán thời gian cho last_login trong DB
    //            entity.setLastLogin(request.getLastLogin());
    //            userRepository.save(entity);
    //            return new ResponseMessage("Đăng nhập thành công");
    //        }
    //    }
    //------------------------------------------- * --------------------------------------------------------------------

    //-----------------------                  Template Quản lý thông số                       -------- ----------------
    //☺ hàm format giá trị thông số
    private static Float FormatValue(Float value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        //        System.out.println("testtttttttttttttttttttttttttttttttttt" + decimalFormat.format(value));
        return Float.parseFloat(decimalFormat.format(value));
    }

    //☺ Hiển thị danh sách thông số
    public List<QuanLyThongSo> danhSachThongSo() {
        //        System.out.println("success !");
        List<QuanLyThongSo> entityList = quanLyThongSoRepository.findAll();
        return entityList;
    }

    //☺ Xoá 1 thông số theo mã thông số
    public void delByThongSo(String maThongSo) {
        List<QuanLyThongSo> entity = quanLyThongSoRepository.findAllByMaThongSo(
            maThongSo
        );
        //        log.info("entity: " + entity);
        if (entity.isEmpty()) {
            log.info("Không tìm thấy thông số");
        } else {
            quanLyThongSoRepository.deleteAll(entity);
            log.info("Xoá thành công !");
        }
    }

    //☺ them moi thong so
    public String postThongSo(List<QuanLyThongSo> requests) {
        for (QuanLyThongSo request : requests) {
            QuanLyThongSo entity = new QuanLyThongSo();
            entity.setMaThongSo(request.getMaThongSo());
            entity.setTenThongSo(request.getTenThongSo());
            entity.setMoTa(request.getMoTa());
            entity.setNgayTao(request.getNgayTao());
            entity.setNgayUpdate(request.getNgayUpdate());
            entity.setUpdateBy(request.getUpdateBy());
            entity.setStatus("deactivate");
            quanLyThongSoRepository.save(entity);
        }
        //        log.info("Them moi thanh cong");
        return "Thêm mới thành công";
    }

    //☺ xem chi tiet thong so
    public List<QuanLyThongSo> getChiTietThongSo(String maThongSo) {
        List<QuanLyThongSo> entities =
            quanLyThongSoRepository.findAllByMaThongSo(maThongSo);
        return entities;
    }

    //☺ cap nhat thong so
    public String putThongSo(QuanLyThongSo request, String maThongSo) {
        QuanLyThongSo entity = quanLyThongSoRepository.getByMaThongSo(
            maThongSo
        );
        entity.setMaThongSo(request.getMaThongSo());
        entity.setTenThongSo(request.getTenThongSo());
        entity.setMoTa(request.getMoTa());
        entity.setNgayUpdate(request.getNgayUpdate());
        entity.setUpdateBy(request.getUpdateBy());
        entity.setStatus(request.getStatus());
        quanLyThongSoRepository.save(entity);
        //        log.info("Cap nhat thanh cong !");
        return "Cap nhat thanh cong !";
    }

    //☺ su kien tim kiem
    public List<QuanLyThongSo> timKiemThongSo(QuanLyThongSo request) {
        var entities = quanLyThongSoRepository.timKiemThongSo(
            request.getMaThongSo(),
            request.getTenThongSo(),
            request.getNgayTao(),
            request.getNgayUpdate(),
            request.getUpdateBy(),
            request.getStatus()
        );
        return entities;
    }

    //---------------------------------------------- * -----------------------------------------------------------------
    //--------------------------------------------          Thiết bị            ----------------------------------------

    //☺ Hiển thị danh sách thiết bị
    public List<ThietBi> danhSachThietBi() {
        List<ThietBi> entities = thietBiRepository.findAll();
        return entities;
    }

    //☺ Tìm kiếm
    public List<ThietBi> timKiemThietBi(ThietBi request) {
        List<ThietBi> entities = thietBiRepository.timKiemThietBi(
            request.getMaThietBi(),
            request.getLoaiThietBi(),
            request.getDayChuyen(),
            request.getNgayTao(),
            request.getTimeUpdate(),
            request.getUpdateBy(),
            request.getStatus()
        );
        return entities;
    }

    //☺ del thiết bị -> xoá luôn cả thông số thiết bị
    public void delThongSoMay(Long id) {
        ThietBi entities = thietBiRepository.findById(id).orElse(null);
        if (entities == null) {
            //            log.info("khong tim thay thiet bi");
        } else {
            List<ThongSoMay> entityList =
                thongSoMayRepository.findAllByThietBiId(id);
            thongSoMayRepository.deleteAll(entityList);
            thietBiRepository.delete(entities);
            //            log.info("xoa thanh cong");
        }
    }

    //----------------------- Chức năng thêm mới thiết bị -----------------------------------------------

    //☺ Lấy thông tin loại thiết bị theo mã thiết bị từ table thiết bị
    // ☺ thêm mới thiết bị vào DB
    public String postThietBi(ThietBi request) {
        //        log.info("them moi thiet bi");
        ThietBi entity = new ThietBi();
        entity.setMaThietBi(request.getMaThietBi());
        entity.setLoaiThietBi(request.getLoaiThietBi());
        entity.setDayChuyen(request.getDayChuyen());
        entity.setNgayTao(request.getNgayTao());
        entity.setTimeUpdate(request.getTimeUpdate());
        entity.setUpdateBy(entity.getUpdateBy());
        entity.setStatus(request.getStatus());
        thietBiRepository.save(entity);
        return "them moi thiet bi thanh cong !";
    }

    //☺ thêm mới thông số thiết b vào DB
    public void postThongSoMay(List<ThongSoMay> requestList) {
        Integer row = 0;
        for (ThongSoMay request : requestList) {
            row += 1;
            ThongSoMay entity = new ThongSoMay();
            entity.setMaThietBi(request.getMaThietBi());
            entity.setLoaiThietBi(request.getLoaiThietBi());
            entity.setHangTms(row);
            entity.setThongSo(request.getThongSo());
            entity.setMoTa(request.getMoTa());
            entity.setTrangThai(request.getTrangThai());
            entity.setPhanLoai(request.getPhanLoai());
            thongSoMayRepository.save(entity);
            // lấy id thiết bị
            ThietBi thietBi = this.thietBiRepository.findAllByLoaiThietBi(
                entity.getLoaiThietBi()
            );
            // update khóa ngoại
            thongSoMayRepository.updateIdThietBi(
                thietBi.getId(),
                entity.getId()
            );
        }
    }

    //----------------------- Chức năng cập nhật thông số thiết bị -----------------------------------------------
    //☺ xem danh sách thông số thiết bị bằng mã thiết bị
    public List<ThongSoMay> getDanhSachThongSoThietBi(String maThietBi) {
        List<ThongSoMay> entities = thongSoMayRepository.findAllByMaThietBi(
            maThietBi
        );
        return entities;
    }

    //☺ xem danh sách thông số thiết bị bằng id
    public List<ThongSoMay> getDanhSachThongSoThietBiById(Long id) {
        List<ThongSoMay> entities = thongSoMayRepository.findAllByThietBiId(id);
        return entities;
    }

    //☺ xem danh sách thông số thiết bị bằng Nhóm thiết bị
    public List<ThongSoMay> getDanhSachThongSoThietBiByLoaiThietBi(
        String loaiThietBi
    ) {
        List<ThongSoMay> entities = thongSoMayRepository.findAllByLoaiThietBi(
            loaiThietBi
        );
        return entities;
    }

    //☺ xem danh sách thông số thiết bị bằng Nhóm thiết bị
    public List<ThongSoMay> getDanhSachThongSoThietBiByLoaiThietBiAndMaThietBi(
        ThongSoMay request
    ) {
        List<ThongSoMay> entities =
            thongSoMayRepository.findAllByLoaiThietBiAndMaThietBi(
                request.getLoaiThietBi(),
                request.getMaThietBi()
            );
        return entities;
    }

    //☺ del thông số thiết bị
    public void delByIdThongSoThietBi(Long idThongSoThietBi) {
        ThongSoMay entity = thongSoMayRepository
            .findById(idThongSoThietBi)
            .orElse(null);
        if (entity == null) {
            String result =
                "=============================   khong tim thay thong so";
            log.info(result);
        } else {
            thongSoMayRepository.delete(entity);
            String result =
                "===========================    xoa thong so may thanh cong";
            log.info(result);
        }
    }

    //☺ cập nhật thông số máy trong khi xem danh sách thông số máy
    public void putThongSoMay(List<ThongSoMay> requestList) {
        // tìm kiếm thông tin thông số theo id_thong_so_thiet_bi
        for (ThongSoMay request : requestList) {
            boolean result = thongSoMayRepository.existsById(request.getId());
            //cập nhật thông số đã có
            if (result == true) {
                ThongSoMay entity = thongSoMayRepository
                    .findById(request.getId())
                    .orElse(null);
                entity.setThongSo(request.getThongSo());
                entity.setMoTa(request.getMoTa());
                entity.setPhanLoai(request.getPhanLoai());
                entity.setTrangThai(request.getTrangThai());
                thongSoMayRepository.save(entity);
            } else {
                // Thêm mới thông số chưa có
                ThongSoMay entity1 = new ThongSoMay();
                entity1.setLoaiThietBi(request.getLoaiThietBi());
                entity1.setMaThietBi(request.getMaThietBi());
                entity1.setThongSo(request.getThongSo());
                entity1.setMoTa(request.getMoTa());
                entity1.setPhanLoai(request.getPhanLoai());
                entity1.setTrangThai(request.getTrangThai());
                thongSoMayRepository.save(entity1);
                // lấy id thiết bị
                ThietBi thietBi = this.thietBiRepository.findAllByLoaiThietBi(
                    entity1.getLoaiThietBi()
                );
                // cập nhật khóa ngoại
                thongSoMayRepository.updateIdThietBi(
                    thietBi.getId(),
                    entity1.getId()
                );
            }
        }
    }

    //☺ xem chi tiết thông số thiet bi
    public ThietBi getAllById(Long id) {
        ThietBi entity = thietBiRepository.getAllById(id);
        //        log.info("entity: " + entity.getThongSoMays());
        return entity;
    }

    //------------------------------------------------ * ---------------------------------------------------------------

    //---------------------------------------              Kịch bản                ------------------------------------

    //☺ Hien thi danh sach kich ban
    public List<KichBan> getDanhSachKichBan() {
        List<KichBan> entities = kichBanRepository.findAll();
        return entities;
    }

    //☺ Tim kiem kich ban
    public List<KichBan> timKiemKichBan(KichBan request) {
        List<KichBan> entities = kichBanRepository.timKiemKichBan(
            request.getMaKichBan(),
            request.getMaThietBi(),
            request.getLoaiThietBi(),
            request.getDayChuyen(),
            request.getMaSanPham(),
            request.getVersionSanPham(),
            request.getNgayTao(),
            request.getTimeUpdate(),
            request.getUpdateBy(),
            request.getTrangThai()
        );
        return entities;
    }

    //☺ Them moi kich ban
    //☺ B1: Thêm mới kịch bản
    public String postKichBan(KichBan request) {
        //        log.info("Them moi kich ban");
        KichBan entity = new KichBan();
        entity.setMaKichBan(request.getMaKichBan());
        entity.setMaThietBi(request.getMaThietBi());
        entity.setLoaiThietBi(request.getLoaiThietBi());
        entity.setDayChuyen(request.getDayChuyen());
        entity.setMaSanPham(request.getMaSanPham());
        entity.setVersionSanPham(request.getVersionSanPham());
        entity.setNgayTao(request.getNgayTao());
        entity.setTimeUpdate(request.getTimeUpdate());
        entity.setUpdateBy(request.getUpdateBy());
        entity.setTrangThai(request.getTrangThai());
        kichBanRepository.save(entity);
        return "Them moi kich ban thanh cong";
    }

    //☺ B2: Thêm mới thông tin thông số kịch bản
    public void postChiTietKichBan(List<ChiTietKichBan> requests) {
        for (ChiTietKichBan request : requests) {
            ChiTietKichBan entity = new ChiTietKichBan();
            entity.setMaKichBan(request.getMaKichBan());
            entity.setTrangThai(request.getTrangThai());
            entity.setThongSo(request.getThongSo());
            entity.setMinValue(request.getMinValue());
            entity.setMaxValue(request.getMaxValue());
            entity.setTrungbinh(request.getTrungbinh());
            entity.setDonVi(request.getDonVi());
            entity.setPhanLoai(request.getPhanLoai());
            chiTietKichBanRepository.save(entity);
            // lấy id kịch bản
            KichBan kichBan = this.kichBanRepository.findAllByMaKichBan(
                entity.getMaKichBan()
            );
            // cập nhật khóa ngoại
            chiTietKichBanRepository.updateIdKichBan(
                kichBan.getId(),
                entity.getId()
            );
        }
    }

    //☺ xem danh sach thong so kich ban theo id kịch bản
    public List<ChiTietKichBan> getAllByIdKichBan(Long kichBanId) {
        List<ChiTietKichBan> entities =
            chiTietKichBanRepository.findAllByKichBanId(kichBanId);
        return entities;
    }

    //☺ xem danh sach thong so kich ban theo mã kịch bản
    public List<ChiTietKichBan> getAllByMaKichBan(String maKichBan) {
        List<ChiTietKichBan> entities = chiTietKichBanRepository.getByMaKichBan(
            maKichBan
        );
        return entities;
    }

    //☺ cap nhat thong so kich ban
    public void putChiTietKichBan(List<ChiTietKichBan> requestList) {
        for (ChiTietKichBan request : requestList) {
            ChiTietKichBan entity = chiTietKichBanRepository
                .findById(request.getId())
                .orElse(null);
            // cap nhat thong so da co
            if (entity != null) {
                entity.setThongSo(request.getThongSo());
                entity.setMinValue(request.getMinValue());
                //                System.out.println("entityyyyyyyyyyyyyyyyyyyyyyyyyyyy: " + entity.getMinValue());
                entity.setMaxValue(FormatValue(request.getMaxValue()));
                entity.setTrungbinh(request.getTrungbinh());
                entity.setDonVi(request.getDonVi());
                chiTietKichBanRepository.save(entity);
            } else {
                // them moi thong so chua co
                ChiTietKichBan entity1 = new ChiTietKichBan();
                entity1.setMaKichBan(request.getMaKichBan());
                entity1.setThongSo(request.getThongSo());
                entity1.setMinValue(request.getMinValue());
                entity1.setMaxValue(request.getMaxValue());
                entity1.setTrungbinh(request.getTrungbinh());
                entity1.setDonVi(request.getDonVi());
                chiTietKichBanRepository.save(entity1);
                // lấy id kịch bản
                KichBan kichBan = this.kichBanRepository.findAllByMaKichBan(
                    entity.getMaKichBan()
                );
                // cập nhật khóa ngoại
                chiTietKichBanRepository.updateIdKichBan(
                    kichBan.getId(),
                    entity1.getId()
                );
            }
        }
    }

    // ☺ xoa kich ban
    public void delKichBan(Long id) {
        KichBan entities = kichBanRepository.findById(id).orElse(null);
        if (entities == null) {
            log.info("khong tim thay kich ban");
        } else {
            // tim kiem thong tin chi tiet kich ban
            List<ChiTietKichBan> entityList =
                chiTietKichBanRepository.findAllByKichBanId(id);
            chiTietKichBanRepository.deleteAll(entityList);
            kichBanRepository.delete(entities);
            log.info("xoa kich ban thanh cong");
        }
    }

    //☺ xoa thong so trong kich ban
    public void delByIdChiTietKichBan(Long idChiTietKichBan) {
        ChiTietKichBan entities = chiTietKichBanRepository
            .findById(idChiTietKichBan)
            .orElse(null);
        if (entities == null) {
            log.info("khong tim thay thong so");
        } else {
            chiTietKichBanRepository.delete(entities);
            log.info("xoa thong so kich ban thanh cong");
        }
    }

    //☺ xem chi tiet kich ban theo id kich ban
    public KichBan chiTietKichBan(Long idKichBan) {
        KichBan entity = kichBanRepository.getAllById(idKichBan);
        //        log.info("sucess !");
        return entity;
    }

    //☺ xem chi tiet kich ban theo ma kich ban
    public KichBan chiTietKichBanByMaKichban(String maKichBan) {
        KichBan entity = kichBanRepository.findAllByMaKichBan(maKichBan);
        //        log.info("sucess !");
        return entity;
    }

    //☺ xem thông tin kịch bản
    public KichBan getKichBanById(Long id) {
        KichBan entity = kichBanRepository.getById(id);
        return entity;
    }

    //----------------------------------------- * ----------------------------------------------------------------------
    //---------------------------                    San xuat hang ngay          ---------------------------------------

    //☺ Hien thi danh sach san xuat hang ngay
    public List<SanXuatHangNgay> getAllSanXuatHangNgay() {
        List<SanXuatHangNgay> entities = sanXuatHangNgayRepository.findAll();
        return entities;
    }

    //☺ Tim kiem noi dung san xuat hang ngay
    public List<SanXuatHangNgay> timKiemSanxuatHangNgay(
        SanXuatHangNgay request
    ) {
        List<SanXuatHangNgay> entities =
            sanXuatHangNgayRepository.timKiemSanXuatHangNgay(
                request.getMaKichBan(),
                request.getMaThietBi(),
                request.getLoaiThietBi(),
                request.getDayChuyen(),
                request.getMaSanPham(),
                request.getVersionSanPham(),
                request.getNgayTao(),
                request.getTimeUpdate(),
                request.getTrangThai()
            );
        //        log.info("" + request);
        return entities;
    }

    // ☺ them moi kich ban san xuat
    public String postSanXuatHangNgay(SanXuatHangNgay request) {
        // them moi kich ban san xuat hang ngay
        SanXuatHangNgay entity = new SanXuatHangNgay();
        entity.setMaKichBan(request.getMaKichBan());
        entity.setMaThietBi(request.getMaThietBi());
        entity.setLoaiThietBi(request.getLoaiThietBi());
        entity.setDayChuyen(request.getDayChuyen());
        entity.setMaSanPham(request.getMaSanPham());
        entity.setVersionSanPham(request.getVersionSanPham());
        entity.setNgayTao(request.getNgayTao());
        entity.setTimeUpdate(request.getTimeUpdate());
        entity.setTrangThai(request.getTrangThai());
        String result1 = "maSanPham";
        String result = entity.getMaSanPham();
        // Note lay danh sach thong so theo ma kich ban tu table chi tiet kich ban
        List<ChiTietKichBan> entities =
            chiTietKichBanRepository.findAllByMaKichBan(request.getMaKichBan());
        List<ChiTietSanXuat> entityList = new ArrayList<>();
        // Note lưu thông tin thông số sản xuất hàng ngày
        for (ChiTietKichBan entity1 : entities) {
            ChiTietSanXuat entity2 = new ChiTietSanXuat();
            entity2.setMaKichBan(entity1.getMaKichBan());
            entity2.setTrangThai(entity1.getTrangThai());
            entity2.setThongSo(entity1.getThongSo());
            entity2.setMinValue(entity1.getMinValue());
            entity2.setMaxValue(entity1.getMaxValue());
            entity2.setTrungbinh(entity1.getTrungbinh());
            entity2.setDonVi(entity1.getDonVi());
            chiTietSanXuatRepository.save(entity2);
            result1 +=
                "|" +
                entity2.getThongSo() +
                "min|" +
                entity2.getThongSo() +
                "max";
            //b3 gán giá trị của thông số
            result += "|" + entity2.getMinValue() + "|" + entity2.getMaxValue();
            //lấy id kịch bản sản xuất hàng ngày
            SanXuatHangNgay sanXuatHangNgay =
                this.sanXuatHangNgayRepository.findAllByMaKichBan(
                    entity2.getMaKichBan()
                );
            // cập nhật khóa ngoại
            chiTietSanXuatRepository.updateIdSanXuatHangNgay(
                sanXuatHangNgay.getId(),
                entity2.getId()
            );
        }
        entity.setParameterConvert(result1);
        entity.setParameterValueConvert(result);
        sanXuatHangNgayRepository.save(entity);
        System.out.println("testtttttttttttttttttt: " + result1);
        System.out.println("testtttttttttttttttttt1: " + result);
        return "Them moi kich ban thanh cong";
    }

    //☺ B2: Thêm mới thông tin thông số kịch bản sản xuất
    public void postChiTietSanXuat(List<ChiTietSanXuat> requests) {
        SanXuatHangNgay sanXuatHangNgay = null;
        String result1 = "";
        String result = "";
        for (ChiTietSanXuat request : requests) {
            if (sanXuatHangNgay == null) {
                sanXuatHangNgay =
                    this.sanXuatHangNgayRepository.findAllByMaKichBan(
                        request.getMaKichBan()
                    );
                result1 = "maSanPham";
                result = sanXuatHangNgay.getMaSanPham();
                System.out.println(
                    "thanh cong !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                );
            }
            ChiTietSanXuat entity = new ChiTietSanXuat();
            entity.setMaKichBan(request.getMaKichBan());
            entity.setTrangThai(request.getTrangThai());
            entity.setThongSo(request.getThongSo());
            entity.setMinValue(request.getMinValue());
            entity.setMaxValue(request.getMaxValue());
            entity.setTrungbinh(request.getTrungbinh());
            entity.setDonVi(request.getDonVi());
            entity.setTrangThai(request.getTrangThai());
            result1 +=
                "|" +
                entity.getThongSo() +
                "min|" +
                entity.getThongSo() +
                "max";
            //b3 gán giá trị của thông số
            result += "|" + entity.getMinValue() + "|" + entity.getMaxValue();
            chiTietSanXuatRepository.save(entity);
            // cập nhật khóa ngoại
            chiTietSanXuatRepository.updateIdSanXuatHangNgay(
                sanXuatHangNgay.getId(),
                entity.getId()
            );
        }
        sanXuatHangNgay.setParameterConvert(result1);
        sanXuatHangNgay.setParameterValueConvert(result);
        sanXuatHangNgayRepository.save(sanXuatHangNgay);
    }

    //☺ xem danh sach thong so san xuat hang ngay theo id san xuat hang ngay
    public List<ChiTietSanXuat> getAllsById(Long id) {
        List<ChiTietSanXuat> entities =
            chiTietSanXuatRepository.findAllBySanXuatHangNgayId(id);
        return entities;
    }

    //☺ Cap nhat noi dung san xuat hang ngay (1)
    public void putChiTietSanXuat(List<ChiTietSanXuat> requestList) {
        SanXuatHangNgay sanXuatHangNgay = null;
        String result1 = "";
        String result = "";
        for (ChiTietSanXuat request : requestList) {
            if (sanXuatHangNgay == null) {
                sanXuatHangNgay =
                    this.sanXuatHangNgayRepository.findAllByMaKichBan(
                        request.getMaKichBan()
                    );
                result1 = "maSanPham";
                result = sanXuatHangNgay.getMaSanPham();
            }
            ChiTietSanXuat entity = chiTietSanXuatRepository
                .findById(request.getId())
                .orElse(null);
            if (entity != null) {
                entity.setThongSo(request.getThongSo());
                entity.setMinValue(request.getMinValue());
                entity.setMaxValue(request.getMaxValue());
                entity.setTrungbinh(request.getTrungbinh());
                entity.setDonVi(request.getDonVi());
                entity.setTrangThai(request.getTrangThai());
                result1 +=
                    "|" +
                    entity.getThongSo() +
                    "min|" +
                    entity.getThongSo() +
                    "max";
                //b3 gán giá trị của thông số
                result +=
                    "|" + entity.getMinValue() + "|" + entity.getMaxValue();
                chiTietSanXuatRepository.save(entity);
            } else {
                ChiTietSanXuat entity1 = new ChiTietSanXuat();
                entity1.setMaKichBan(request.getMaKichBan());
                entity1.setThongSo(request.getThongSo());
                entity1.setMinValue(request.getMinValue());
                entity1.setMaxValue(request.getMaxValue());
                entity1.setTrungbinh(request.getTrungbinh());
                entity1.setDonVi(request.getDonVi());
                entity1.setTrangThai(request.getTrangThai());
                result1 +=
                    "|" +
                    entity1.getThongSo() +
                    "min|" +
                    entity1.getThongSo() +
                    "max";
                //b3 gán giá trị của thông số
                result +=
                    "|" + entity1.getMinValue() + "|" + entity1.getMaxValue();
                chiTietSanXuatRepository.save(entity1);
                chiTietSanXuatRepository.updateIdSanXuatHangNgay(
                    sanXuatHangNgay.getId(),
                    entity1.getId()
                );
            }
        }
        sanXuatHangNgay.setParameterConvert(result1);
        sanXuatHangNgay.setParameterValueConvert(result);
        sanXuatHangNgayRepository.save(sanXuatHangNgay);
    }

    // ☺ (1)xoa thong so trong noi dung san xuat hang ngay
    public void delByIdChiTietSanXuat(Long idChiTietSanXuat) {
        ChiTietSanXuat entity = chiTietSanXuatRepository
            .findById(idChiTietSanXuat)
            .orElse(null);
        if (entity == null) {
            log.info("khong tim thay thong so");
        } else {
            chiTietSanXuatRepository.delete(entity);
            log.info("xoa thong so thanh cong");
        }
    }

    //☺ xem chi tiet noi dung 1 kich ban san xuat hang ngay
    public SanXuatHangNgay chiTietSanXuat(Long maKichBan) {
        SanXuatHangNgay entity = sanXuatHangNgayRepository
            .findById(maKichBan)
            .orElse(null);
        log.info("thanh cong");
        return entity;
    }

    // ☺ xoa kich ban trong sasn xuat hang ngay
    public void delSanXuatHangNgay(Long id) {
        SanXuatHangNgay entities = sanXuatHangNgayRepository
            .findById(id)
            .orElse(null);
        if (entities == null) {
            log.info("khong tim thay kich ban");
        } else {
            // tim kiem thong tin chi tiet kich ban
            List<ChiTietSanXuat> entityList =
                chiTietSanXuatRepository.findAllBySanXuatHangNgayId(id);
            chiTietSanXuatRepository.deleteAll(entityList);
            sanXuatHangNgayRepository.delete(entities);
            //            log.info("xoa kich ban thanh cong");
        }
    }

    //☺ xem thông số kịch bản sản xuất theo san_xuat_hang_ngay_id
    public List<ChiTietSanXuat> getChiTietSanXuatBySanXuatHangNgayId(
        Long sanXuatHangNgayId
    ) {
        List<ChiTietSanXuat> chiTietSanXuatList =
            this.chiTietSanXuatRepository.findAllBySanXuatHangNgayId(
                sanXuatHangNgayId
            );
        return chiTietSanXuatList;
    }

    //☺ thay đổi signal khi có sự thay đổi về chi tiết sản xuất
    public void changeSignal(Long id, SanXuatHangNgay request) {
        SanXuatHangNgay entity = this.sanXuatHangNgayRepository.findById(
            id
        ).orElse(null);
        if (entity != null) {
            entity.setSignal(request.getSignal());
            this.sanXuatHangNgayRepository.save(entity);
        }
    }

    //☺ thay doi signal theo makich ban va signal
    public void changeSignal2(SanXuatHangNgay request) {
        SanXuatHangNgay entity =
            this.sanXuatHangNgayRepository.findByMaKichBanAndSignal(
                request.getMaKichBan(),
                request.getSignal()
            );
        entity.setSignal(Long.valueOf(1));
        this.sanXuatHangNgayRepository.save(entity);
    }

    //☺ tìm kiếm mã kịch bản sản xuất theo signal
    public List<SanXuatHangNgay> findAllBySignal(Long signal) {
        List<SanXuatHangNgay> sanXuatHangNgayList =
            this.sanXuatHangNgayRepository.findAllBySignal(signal);
        return sanXuatHangNgayList;
    }

    //☺ tim kiem thong tin kich ban theo ma kich ban
    public SanXuatHangNgay getSXHNByMaKichBan(String maKichBan) {
        SanXuatHangNgay sanXuatHangNgay =
            this.sanXuatHangNgayRepository.findAllByMaKichBan(maKichBan);
        return sanXuatHangNgay;
    }

    //------------------------------------------------------ *  --------------------------------------------------------
    //------------------------------------------------ Nhóm thiết bị ---------------------------------------------------
    public List<NhomThietBi> getAllNhomThietBi() {
        List<NhomThietBi> nhomThietBis = this.nhomThietBiRepository.findAll();
        return nhomThietBis;
    }

    public List<NhomThietBi> insertNhomThietBi(List<NhomThietBi> nhomThietBis) {
        for (NhomThietBi nhomThietBi : nhomThietBis) {
            this.nhomThietBiRepository.save(nhomThietBi);
        }
        return nhomThietBis;
    }

    //☺ update nhom thiet bi
    public void updateNhomThietBiNew(List<NhomThietBi> nhomThietBi) {
        for (NhomThietBi nhomThietBi1 : nhomThietBi) {
            this.nhomThietBiRepository.updateNhomThietBiNew(
                nhomThietBi1.getGroupId(),
                nhomThietBi1.getId()
            );
        }
    }

    //☺ Lấy danh sach thiet bi theo group id
    public List<NhomThietBi> getAllByGroupId(Long groupId) {
        List<NhomThietBi> nhomThietBis =
            this.nhomThietBiRepository.findAllByGroupId(groupId);
        return nhomThietBis;
    }

    //------------------------------------------------------ *  --------------------------------------------------------
    //------------------------------------------------ Don vi---------------------------------------------------
    public List<DonVi> getAllDonVi() {
        List<DonVi> donVis = this.donViRepository.findAll();
        return donVis;
    }

    //--------------------------------------------------- * ------------------------------------------------------------
    //-------------------------------------- Day chuyen ------------------------------------
    public List<DayChuyen> getAllDayChuyen() {
        List<DayChuyen> dayChuyenList = this.dayChuyenRepository.findAll();
        return dayChuyenList;
    }

    //---------------------------------------------- * -----------------------------------------------------------------
    //------------------------------- Kich ban change status -----------------------------------------
    //☺ lay danh sach kich ban change status
    public List<KichBanChangeStatus> findAllKBCS() {
        List<KichBanChangeStatus> entityList =
            this.kichBanChangeStatusRepository.findAll();
        return entityList;
    }

    //☺ cap nhat color_change theo ma kich ban
    public KichBanChangeStatus updateKBCS(KichBanChangeStatus request) {
        KichBanChangeStatus entity =
            this.kichBanChangeStatusRepository.findByMaKichBan(
                request.getMaKichBan()
            );
        entity.setColorChange(request.getColorChange());
        this.kichBanChangeStatusRepository.save(entity);
        return entity;
    }

    //-------------------------------------------- * -------------------------------------------------------------------
    // ------------------------ lenh san xuat
    //☺ lấy danh sách lệnh sản xuất
    public List<LenhSanXuat> getAllLenhSanXuat() {
        List<LenhSanXuat> entities = this.lenhSanXuatRepository.findAll();
        return entities;
    }

    //☺ tìm kiếm lệnh sản xuất
    public List<LenhSanXuat> timKiemLenhSanXuat(LenhSanXuat request) {
        List<LenhSanXuat> entities =
            this.lenhSanXuatRepository.timKiemLenhSanXuat(
                request.getMaLenhSanXuat(),
                request.getSapCode(),
                request.getSapName(),
                request.getWorkOrderCode(),
                request.getVersion(),
                request.getStorageCode(),
                request.getCreateBy(),
                request.getTrangThai()
            );
        return entities;
    }

    //☺ Tìm kiếm lệnh sản xuất đi kèm với phân trang
    public List<LenhSanXuat> timKiemQuanLyPheDuyet(TemInDTO temInDTO) {
        LocalDate date = LocalDate.now();
        LocalDate firstDay = LocalDate.of(
            LocalDate.now().getYear(),
            LocalDate.now().getMonth(),
            1
        );
        String entryTime1 = firstDay.toString() + " 00:00:00.000";
        String entryTime2 = date.toString() + " 23:59:59.000";
        String timeUpdate1 = firstDay.toString() + " 00:00:00.000";
        String timeUpdate2 = date.toString() + " 23:59:59.000";
        if (temInDTO.getEntryTime() != null) {
            entryTime1 = temInDTO.getEntryTime() + " 00:00:00.000";
            entryTime2 = temInDTO.getEntryTime() + " 23:59:59.000";
        }
        if (temInDTO.getTimeUpdate() != null) {
            timeUpdate1 = temInDTO.getTimeUpdate() + " 00:00:00.000";
            timeUpdate2 = temInDTO.getTimeUpdate() + " 23:59:59.000";
        }
        List<LenhSanXuat> entities =
            this.lenhSanXuatRepository.timKiemQuanLyPheDuyet(
                "%".concat(temInDTO.getMaLenhSanXuat()).concat("%"),
                "%".concat(temInDTO.getSapCode()).concat("%"),
                "%".concat(temInDTO.getSapName()).concat("%"),
                "%".concat(temInDTO.getWorkOrderCode()).concat("%"),
                "%".concat(temInDTO.getVersion()).concat("%"),
                "%".concat(temInDTO.getStorageCode()).concat("%"),
                "%".concat(temInDTO.getCreateBy()).concat("%"),
                entryTime1,
                entryTime2,
                timeUpdate1,
                timeUpdate2,
                "%".concat(temInDTO.getTrangThai()).concat("%"),
                (temInDTO.getPageNumber() - 1) * temInDTO.getItemPerPage(),
                temInDTO.getItemPerPage()
            );
        return entities;
    }

    //☺ Lấy thông tin tổng dữ liệu
    public Integer totalData(TemInDTO temInDTO) {
        LocalDate date = LocalDate.now();
        LocalDate firstDay = LocalDate.of(
            LocalDate.now().getYear(),
            LocalDate.now().getMonth(),
            1
        );
        String entryTime1 = firstDay.toString() + " 00:00:00.000";
        String entryTime2 = date.toString() + " 23:59:59.000";
        String timeUpdate1 = firstDay.toString() + " 00:00:00.000";
        String timeUpdate2 = date.toString() + " 23:59:59.000";
        if (temInDTO.getEntryTime() != null) {
            entryTime1 = temInDTO.getEntryTime() + " 00:00:00.000";
            entryTime2 = temInDTO.getEntryTime() + " 23:59:59.000";
        }
        if (temInDTO.getTimeUpdate() != null) {
            timeUpdate1 = temInDTO.getTimeUpdate() + " 00:00:00.000";
            timeUpdate2 = temInDTO.getTimeUpdate() + " 23:59:59.000";
        }
        Integer totalData = this.lenhSanXuatRepository.totalData(
            "%".concat(temInDTO.getMaLenhSanXuat()).concat("%"),
            "%".concat(temInDTO.getSapCode()).concat("%"),
            "%".concat(temInDTO.getSapName()).concat("%"),
            "%".concat(temInDTO.getWorkOrderCode()).concat("%"),
            "%".concat(temInDTO.getVersion()).concat("%"),
            "%".concat(temInDTO.getStorageCode()).concat("%"),
            "%".concat(temInDTO.getCreateBy()).concat("%"),
            entryTime1,
            entryTime2,
            timeUpdate1,
            timeUpdate2,
            "%".concat(temInDTO.getTrangThai()).concat("%")
        );
        return totalData;
    }

    //☺ Tìm kiếm thông tin tem sản xuất đi kèm với phân trang
    public List<LenhSanXuat> timKiemThongTinTemSanXuat(TemInDTO temInDTO) {
        LocalDate date = LocalDate.now();
        LocalDate firstDay = LocalDate.of(
            LocalDate.now().getYear(),
            LocalDate.now().getMonth(),
            1
        );
        LocalDate thirtyDaysAgo = date.minusDays(29); // Bao gồm hôm nay = 30 ngày
        String entryTime1 = thirtyDaysAgo.toString() + " 00:00:00.000";
        String entryTime2 = date.toString() + " 23:59:59.000";
        if (temInDTO.getEntryTime() != null) {
            entryTime1 = temInDTO.getEntryTime() + " 00:00:00.000";
            entryTime2 = temInDTO.getEntryTime() + " 23:59:59.000";
        }
        List<LenhSanXuat> entities =
            this.lenhSanXuatRepository.timKiemThongTinTemSanXuat(
                "%".concat(temInDTO.getMaLenhSanXuat()).concat("%"),
                "%".concat(temInDTO.getSapCode()).concat("%"),
                "%".concat(temInDTO.getSapName()).concat("%"),
                "%".concat(temInDTO.getWorkOrderCode()).concat("%"),
                "%".concat(temInDTO.getVersion()).concat("%"),
                "%".concat(temInDTO.getStorageCode()).concat("%"),
                "%".concat(temInDTO.getCreateBy()).concat("%"),
                entryTime1,
                entryTime2,
                "%".concat(temInDTO.getTrangThai()).concat("%"),
                (temInDTO.getPageNumber() - 1) * temInDTO.getItemPerPage(),
                temInDTO.getItemPerPage()
            );
        return entities;
    }

    //☺ Lấy thông tin tổng dữ liệu
    public Integer totalDataThongTinTemSanXuat(TemInDTO temInDTO) {
        LocalDate date = LocalDate.now();
        LocalDate firstDay = LocalDate.of(
            LocalDate.now().getYear(),
            LocalDate.now().getMonth(),
            1
        );
        LocalDate thirtyDaysAgo = date.minusDays(29); // Bao gồm hôm nay = 30 ngày
        String entryTime1 = thirtyDaysAgo.toString() + " 00:00:00.000";
        String entryTime2 = date.toString() + " 23:59:59.000";

        if (temInDTO.getEntryTime() != null) {
            entryTime1 = temInDTO.getEntryTime() + " 00:00:00.000";
            entryTime2 = temInDTO.getEntryTime() + " 23:59:59.000";
        }
        Integer totalData =
            this.lenhSanXuatRepository.totalDataThongTinTemSanXuat(
                "%".concat(temInDTO.getMaLenhSanXuat()).concat("%"),
                "%".concat(temInDTO.getSapCode()).concat("%"),
                "%".concat(temInDTO.getSapName()).concat("%"),
                "%".concat(temInDTO.getWorkOrderCode()).concat("%"),
                "%".concat(temInDTO.getVersion()).concat("%"),
                "%".concat(temInDTO.getStorageCode()).concat("%"),
                "%".concat(temInDTO.getCreateBy()).concat("%"),
                entryTime1,
                entryTime2,
                "%".concat(temInDTO.getTrangThai()).concat("%")
            );
        return totalData;
    }

    //☺ xem chi tiet lenh san xuat theo ma lenh san xuat id
    public List<ChiTietLenhSanXuat> chiTietLenhSanXuat(Long maLenhSanXuat) {
        List<ChiTietLenhSanXuat> entities =
            this.chiTietLenhSanXuatRepository.getAllByMaLenhSanXuatId(
                maLenhSanXuat
            );
        return entities;
    }

    //☺ Cap nhat trang thai cua lenh san xuat
    public void updateTrangThaiLenhSanXuat(Long id, LenhSanXuat request) {
        LenhSanXuat lenhSanXuat = this.lenhSanXuatRepository.findById(
            id
        ).orElse(null);
        if (lenhSanXuat != null) {
            lenhSanXuat.setTimeUpdate(request.getTimeUpdate());
            lenhSanXuat.setTrangThai(request.getTrangThai());
            lenhSanXuat.setCreateBy(request.getCreateBy());
            lenhSanXuatRepository.save(lenhSanXuat);
        }
    }

    //☺ cập nhật chi tiết lệnh sản xuất
    public void updateChiTietLenhSanXuat(
        List<ChiTietLenhSanXuat> request,
        Long id
    ) {
        for (ChiTietLenhSanXuat chiTietLenhSanXuat : request) {
            ChiTietLenhSanXuat entity =
                this.chiTietLenhSanXuatRepository.findById(
                    chiTietLenhSanXuat.getId()
                ).orElse(null);
            if (entity != null) {
                entity.setInitialQuantity(
                    chiTietLenhSanXuat.getInitialQuantity()
                );
                entity.setStorageUnit(chiTietLenhSanXuat.getStorageUnit());
                entity.setSubStorageUnit(
                    chiTietLenhSanXuat.getSubStorageUnit()
                );
                entity.setTrangThai(chiTietLenhSanXuat.getTrangThai());
                entity.setChecked(chiTietLenhSanXuat.getChecked());
                this.chiTietLenhSanXuatRepository.save(entity);
            } else {
                this.chiTietLenhSanXuatRepository.save(chiTietLenhSanXuat);
                ChiTietLenhSanXuat chiTietLenhSanXuat1 =
                    this.chiTietLenhSanXuatRepository.getChiTietLenhSanXuatItem(
                        chiTietLenhSanXuat.getReelID()
                    );
                this.chiTietLenhSanXuatRepository.updateMaLenhSanXuatId(
                    id,
                    chiTietLenhSanXuat1.getId()
                );
            }
        }
    }

    //☺lay danh sach ma lenh san xuat
    public List<String> getListMaLenhSanXuat() {
        return this.lenhSanXuatRepository.getListMaLenhSanXuat();
    }

    public List<String> getListSapCode() {
        return this.lenhSanXuatRepository.getListSapCode();
    }

    public List<String> getListSapName() {
        return this.lenhSanXuatRepository.getListSapName();
    }

    public List<String> getListWorkOrderCode() {
        return this.lenhSanXuatRepository.getListWorkOrderCode();
    }

    public List<String> getListVersion() {
        return this.lenhSanXuatRepository.getListVersion();
    }

    //---------------------------------------- * ----------------------------------------------------------------------
    // ---------------------- Nhom san pham ---------------------
    public List<String> getAllNhomSanPham() {
        List<String> nhomSanPhams =
            this.nhomSanPhamRepository.getAllNhomSanPham();
        System.out.println("thanh cong :                 " + nhomSanPhams);
        return nhomSanPhams;
    }

    // * -------------------------------- scan service ------------------------------------------------------------
    // ? Quản lý thiết bị
    // ?Lấy danh sách thiết bị theo nhóm thiết bị
    public List<scanMachines> listMachines(Long groupId) {
        List<scanMachines> scanMachinesList =
            this.scanMachinesRepository.listMachines(groupId);
        return scanMachinesList;
    }

    //☺Thêm mới list thiết bị theo nhóm thiết bị
    public void insertScanMachines(List<scanMachines> scanMachinesList) {
        for (scanMachines scanMachines : scanMachinesList) {
            this.scanMachinesRepository.postListMachines(
                scanMachines.getMachineName(),
                scanMachines.getGroupId()
            );
        }
    }

    //☺Chỉnh sửa thông tin thiết bị theo nhóm thiết bị
    public void putListMachines(List<scanMachines> scanMachinesList) {
        for (scanMachines scanMachines : scanMachinesList) {
            this.scanMachinesRepository.putListMachines(
                scanMachines.getMachineName(),
                scanMachines.getGroupId(),
                scanMachines.getGroupId()
            );
        }
    }

    //☺Lấy danh sách nhóm thiết bị
    public List<scanGroupMachines> groupMachinesList() {
        List<scanGroupMachines> scanGroupMachinesList =
            this.scanGroupMachinesRepository.groupMachinesList();
        return scanGroupMachinesList;
    }

    //☺Thêm mới nhóm thiết bị
    public Long insertGroupMachines(groupMachineDTO scanGroupMachines) {
        this.scanGroupMachinesRepository.insertGroupMachines(
            scanGroupMachines.getGroupName(),
            scanGroupMachines.getCreateAt(),
            scanGroupMachines.getUsername(),
            scanGroupMachines.getGroupStatus()
        );
        Long groupId = this.scanGroupMachinesRepository.getAllByGroupName(
            scanGroupMachines.getGroupName()
        );
        return groupId;
    }

    //☺Chỉnh sửa thông tin nhóm thiết bị
    public void putGroupMachines(groupMachineDTO scanGroupMachines) {
        this.scanGroupMachinesRepository.putGroupMachines(
            scanGroupMachines.getGroupName(),
            scanGroupMachines.getUpdateAt(),
            scanGroupMachines.getUsername(),
            scanGroupMachines.getGroupStatus(),
            scanGroupMachines.getGroupId()
        );
    }

    //☺Lấy danh sách sản phẩm
    public List<scanProduct> listProduct() {
        List<scanProduct> scanProducts =
            this.scanProductRepository.listProduct();
        return scanProducts;
    }

    //☺ Lấy danh sách machine
    public List<scanMachines> listAllMachines() {
        List<scanMachines> scanMachinesList =
            this.scanMachinesRepository.listAllMachines();
        return scanMachinesList;
    }

    //☺Lấy danh sách tiêu chí đã khai báo theo sản phẩm
    public List<ScanPprofileCheck> listProfileCheckByProduct(Long productId) {
        List<ScanPprofileCheck> scanPprofileChecks =
            this.scanprofileCheckRepository.listProfileCheckByProduct(
                productId
            );
        return scanPprofileChecks;
    }

    //☺Thêm mới thông tin tiêu chí khai báo cho sản phẩm
    public void insertScanProfileCheck(
        List<ScanPprofileCheck> scanPprofileChecks
    ) {
        for (ScanPprofileCheck scanPprofileCheck : scanPprofileChecks) {
            this.scanprofileCheckRepository.insertScanProfileCheck(
                scanPprofileCheck.getProductId(),
                scanPprofileCheck.getCheckName(),
                scanPprofileCheck.getCheckValue(),
                scanPprofileCheck.getCheckStatus(),
                scanPprofileCheck.getPosition(),
                scanPprofileCheck.getVersionId(),
                scanPprofileCheck.getMachineId()
            );
        }
    }

    //☺ chỉnh sửa thông tin tiêu chí khai báo cho sản phẩm
    public void updateScanProfileCheck(
        List<ProfileCheckDTO> scanPprofileChecks
    ) {
        for (ProfileCheckDTO scanPprofileCheck : scanPprofileChecks) {
            if (scanPprofileCheck.getChecked() != null) {
                this.scanprofileCheckRepository.insertProfileCheck(
                    scanPprofileCheck.getProductId(),
                    scanPprofileCheck.getCheckName(),
                    scanPprofileCheck.getCheckValue(),
                    scanPprofileCheck.getCheckStatus(),
                    scanPprofileCheck.getPosition(),
                    scanPprofileCheck.getVersionId(),
                    scanPprofileCheck.getMachineId()
                );
            } else {
                this.scanprofileCheckRepository.updateScanProfileCheck(
                    scanPprofileCheck.getProductId(),
                    scanPprofileCheck.getCheckName(),
                    scanPprofileCheck.getCheckValue(),
                    scanPprofileCheck.getCheckStatus(),
                    scanPprofileCheck.getPosition(),
                    scanPprofileCheck.getVersionId(),
                    scanPprofileCheck.getMachineId(),
                    scanPprofileCheck.getProfileId()
                );
            }
        }
    }

    //☺Lưu thông tin kết quả scan theo chu kỳ ( 10 kết quả) theo id lệnh sản xuất (OrderID)
    public void insertDetailCheck(List<detailCheckDTO> scanDetailChecks) {
        for (detailCheckDTO scanDetailCheck1 : scanDetailChecks) {
            this.scanDetailCheckRepository.insertDetailCheck(
                scanDetailCheck1.getOrderId(),
                scanDetailCheck1.getRecordValue(),
                scanDetailCheck1.getResult(),
                scanDetailCheck1.getPosition(),
                scanDetailCheck1.getUsername(),
                scanDetailCheck1.getMachineId(),
                scanDetailCheck1.getRecordName(),
                scanDetailCheck1.getCreateAt()
            );
        }
    }

    //☺Lấy thông tin lịch sử đăng nhập theo lệnh sản xuất
    public List<scanLoginHistory> listLoginByWorkOrder(Long orderId) {
        List<scanLoginHistory> scanLoginHistories =
            this.scanLoginHistoryRepository.listLoginByWorkOrder(orderId);
        return scanLoginHistories;
    }

    //☺Lấy thông tin chi tiết scan sản phẩm theo lệnh sản xuất
    public List<TongHopResponse> listDetailCheckByWorkOrder(Long orderId) {
        List<TongHopResponse> scanDetailChecks =
            this.scanDetailCheckRepository.listDetailCheckByWorkOrder(orderId);
        return scanDetailChecks;
    }

    //☺Lấy thông tin lệnh sản xuất theo group máy
    public List<workOrderInfo> listWorkOrderByGroup() {
        List<workOrderInfo> scanWorkorders =
            this.scanWorkOrderRepository.listWorkOrderByGroup();
        return scanWorkorders;
    }

    //☺Cập nhật trạng thái lệnh sản xuất
    public void updateWorkingWorkOrder(scanWorkorder scanWorkorder) {
        this.scanWorkOrderRepository.updateWorkingWorkOrder(
            scanWorkorder.getWorking(),
            scanWorkorder.getOrderId()
        );
    }

    //☺Lấy thông tin lệnh sản xuất theo orderId
    public workOrderInfo listWorkOrderByGroupById(Long orderId) {
        workOrderInfo workOrderInfo =
            this.scanWorkOrderRepository.listWorkOrderByGroupById(orderId);
        return workOrderInfo;
    }

    //☺ lấy thông tin profile check
    public List<TongHopResponse> listProfileCheck(Long productId) {
        List<TongHopResponse> scanPprofileCheck =
            this.scanprofileCheckRepository.listProfileCheck(productId);
        return scanPprofileCheck;
    }

    public List<TongHopResponse> tongHop(Long orderId) {
        List<TongHopResponse> tongHopResponses =
            this.scanDetailCheckRepository.tongHop(orderId);
        return tongHopResponses;
    }

    //☺ lay tong so luong
    public List<LenhSanXuatDTO> getTongSoLuong(List<LenhSanXuat> lenhSanXuats) {
        List<LenhSanXuatDTO> tongHopResponses = new ArrayList<>();
        for (LenhSanXuat lenhSanXuat : lenhSanXuats) {
            LenhSanXuatDTO lenhSanXuatDTO = new LenhSanXuatDTO();
            lenhSanXuatDTO.setTongSoLuong(
                this.chiTietLenhSanXuatRepository.getTongSoLuong(
                    lenhSanXuat.getId()
                )
            );
            tongHopResponses.add(lenhSanXuatDTO);
        }
        return tongHopResponses;
    }

    //☺ luu thong tin login
    public void saveLoginHistoryInfo(scanLoginHistory scanLoginHistory) {
        this.scanLoginHistoryRepository.saveLoginHistoryInfo(
            scanLoginHistory.getUsername(),
            scanLoginHistory.getTimeLogin(),
            scanLoginHistory.getOrderId()
        );
    }

    //☺ Cap nhat trang thai working
    public void updateWorkOrderWorking(scanWorkorder scanWorkorder) {
        this.scanWorkOrderRepository.updateWorkOrderWorking(
            scanWorkorder.getWorking(),
            scanWorkorder.getRunTime(),
            scanWorkorder.getOrderId()
        );
    }

    // * ------------------------------------------ Version ----------------------------
    //☺ Lấy thông tin version theo productId
    public List<scanProductVersions> getListVersionsByProductId(
        Long productId
    ) {
        List<scanProductVersions> scanProductVersionsList =
            this.scanProductVersionRepository.getAllByProductId(productId);
        return scanProductVersionsList;
    }

    //☺ Them moi thong tin version
    public scanProductVersions createVersion(
        scanProductVersions scanProductVersionsList
    ) {
        scanProductVersions scanProductVersions =
            this.scanProductVersionRepository.getAllByVersionId(
                scanProductVersionsList.getVersionId()
            );
        if (scanProductVersions == null) {
            this.scanProductVersionRepository.save(scanProductVersionsList);
        } else {
            scanProductVersions.setVersion(
                scanProductVersionsList.getVersion()
            );
            scanProductVersions.setUpdateAt(
                scanProductVersionsList.getUpdateAt()
            );
            scanProductVersions.setUsername(
                scanProductVersionsList.getUsername()
            );
            scanProductVersions.setGroupId(
                scanProductVersionsList.getGroupId()
            );
            this.scanProductVersionRepository.save(scanProductVersions);
        }
        return scanProductVersionsList;
    }

    //☺ Lấy thông tin version theo productId và versionId
    // * ------------------------- Quản lý trạm thông tin kiểm tra ------------------
    //☺ Lấy danh sách sản phẩm + phân trang + lọc
    public List<scanProduct> getListProduct(scanProductDTO scanProductDTO) {
        LocalDate date = LocalDate.now();
        LocalDate firstDay = LocalDate.of(
            LocalDate.now().getYear(),
            LocalDate.now().getMonth(),
            1
        );
        String entryTime1 = firstDay.toString() + " 00:00:00.000";
        String entryTime2 = date.toString() + " 23:59:59.000";
        if (scanProductDTO.getCreatedAt() != null) {
            entryTime1 = scanProductDTO.getCreatedAt() + " 00:00:00.000";
            entryTime2 = scanProductDTO.getCreatedAt() + " 23:59:59.000";
        }
        List<scanProduct> scanProducts =
            this.scanProductRepository.getListProduct(
                "%" + scanProductDTO.getProductCode() + "%",
                "%" + scanProductDTO.getProductName() + "%",
                entryTime1,
                entryTime2,
                "%" + scanProductDTO.getUsername() + "%",
                (scanProductDTO.getPageNumber() - 1) *
                scanProductDTO.getItemPerPage(),
                scanProductDTO.getItemPerPage()
            );
        return scanProducts;
    }

    //☺ Lấy tổng phần tử
    public Integer getTotalItemPD(scanProductDTO scanProductDTO) {
        LocalDate date = LocalDate.now();
        LocalDate firstDay = LocalDate.of(
            LocalDate.now().getYear(),
            LocalDate.now().getMonth(),
            1
        );
        String entryTime1 = firstDay.toString() + " 00:00:00.000";
        String entryTime2 = date.toString() + " 23:59:59.000";
        if (scanProductDTO.getCreatedAt() != null) {
            entryTime1 = scanProductDTO.getCreatedAt() + " 00:00:00.000";
            entryTime2 = scanProductDTO.getCreatedAt() + " 23:59:59.000";
        }
        Integer scanProducts = this.scanProductRepository.getTotalItem(
            "%" + scanProductDTO.getProductCode() + "%",
            "%" + scanProductDTO.getProductName() + "%",
            entryTime1,
            entryTime2,
            "%" + scanProductDTO.getUsername() + "%"
        );
        return scanProducts;
    }

    // * -------------------------- Kiểm soát đối chiếu lệnh sản xuất -------------------------
    //☺ Lấy danh sách lệnh sản xuất + phân trang+ tìm kiếm + sort
    public List<workOrderInfo> getListWorkOrders(
        ScanWorkOrderDTO scanWorkOrderDTO
    ) {
        LocalDate date = LocalDate.now();
        LocalDate firstDay = LocalDate.of(
            LocalDate.now().getYear(),
            LocalDate.now().getMonth(),
            1
        );
        String entryTime1 = firstDay.toString() + " 00:00:00.000";
        String entryTime2 = date.toString() + " 23:59:59.000";
        if (scanWorkOrderDTO.getCreateAt() != null) {
            entryTime1 = scanWorkOrderDTO.getCreateAt() + " 00:00:00.000";
            entryTime2 = scanWorkOrderDTO.getCreateAt() + " 23:59:59.000";
        }
        List<workOrderInfo> workOrderInfos =
            this.scanWorkOrderRepository.getListWorkOrders(
                "%" + scanWorkOrderDTO.getWorkOrder() + "%",
                "%" + scanWorkOrderDTO.getLot() + "%",
                "%" + scanWorkOrderDTO.getProductCode() + "%",
                "%" + scanWorkOrderDTO.getProductName() + "%",
                "%" + scanWorkOrderDTO.getGroupName() + "%",
                entryTime1,
                entryTime2,
                (scanWorkOrderDTO.getPageNumber() - 1) *
                scanWorkOrderDTO.getItemPerPage(),
                scanWorkOrderDTO.getItemPerPage()
            );
        return workOrderInfos;
    }

    //☺ Lấy tổng item
    public Integer getToTalItemWorkOrders(ScanWorkOrderDTO scanWorkOrderDTO) {
        LocalDate date = LocalDate.now();
        LocalDate firstDay = LocalDate.of(
            LocalDate.now().getYear(),
            LocalDate.now().getMonth(),
            1
        );
        String entryTime1 = firstDay.toString() + " 00:00:00.000";
        String entryTime2 = date.toString() + " 23:59:59.000";
        if (scanWorkOrderDTO.getCreateAt() != null) {
            entryTime1 = scanWorkOrderDTO.getCreateAt() + " 00:00:00.000";
            entryTime2 = scanWorkOrderDTO.getCreateAt() + " 23:59:59.000";
        }
        Integer workOrderInfos = this.scanWorkOrderRepository.getTotalData(
            "%" + scanWorkOrderDTO.getWorkOrder() + "%",
            "%" + scanWorkOrderDTO.getLot() + "%",
            "%" + scanWorkOrderDTO.getProductCode() + "%",
            "%" + scanWorkOrderDTO.getProductName() + "%",
            "%" + scanWorkOrderDTO.getGroupName() + "%",
            entryTime1,
            entryTime2
        );
        return workOrderInfos;
    }

    //☺ Lấy tổng pass , Ng
    public List<DetailCheckResponse> getTotalPassNg(
        ScanWorkOrderDTO scanWorkOrderDTO
    ) {
        List<DetailCheckResponse> detailCheckResponses =
            this.scanDetailCheckRepository.getTotalPassNg(
                (scanWorkOrderDTO.getPageNumber() - 1) *
                scanWorkOrderDTO.getItemPerPage(),
                scanWorkOrderDTO.getItemPerPage()
            );
        return detailCheckResponses;
    }

    // * ---------------------------- Profile check ----------------------------
    //☺ Thêm mới sản phẩm
    public scanProduct createProduct(scanProduct scanProduct) {
        this.scanProductRepository.save(scanProduct);
        return scanProduct;
    }

    // * ------------------------- Product version ---------------------------------
    //☺ Lấy danh sách profile theo product
    public List<ProfileCheckResponse> getProfileCheckInfo(Long productId) {
        List<ProfileCheckResponse> profileCheckResponses =
            this.scanprofileCheckRepository.getProfileCheckInfo(productId);
        return profileCheckResponses;
    }

    //☺ Lấy danh sách profile theo product và version
    public List<ProfileCheckResponse> getProfileCheckInfoWithVersionId(
        ScanPprofileCheck scanPprofileCheck
    ) {
        List<ProfileCheckResponse> profileCheckResponses =
            this.scanprofileCheckRepository.getProfileCheckInfoWithVersionId(
                scanPprofileCheck.getProductId(),
                scanPprofileCheck.getVersionId()
            );
        return profileCheckResponses;
    }

    //☺ Thêm mới profile check
    public void insertProfileCheck(List<ScanPprofileCheck> scanPprofileChecks) {
        for (ScanPprofileCheck scanPprofileCheck : scanPprofileChecks) {
            this.scanprofileCheckRepository.insertProfileCheck(
                scanPprofileCheck.getProductId(),
                scanPprofileCheck.getCheckName(),
                scanPprofileCheck.getCheckValue(),
                scanPprofileCheck.getCheckStatus(),
                scanPprofileCheck.getPosition(),
                scanPprofileCheck.getVersionId(),
                scanPprofileCheck.getMachineId()
            );
        }
    }

    // * ---------------------------- export -----------------------
    public List<TongHopResponse> getExportInfo(Long orderId) {
        List<TongHopResponse> tongHopResponses =
            this.scanDetailCheckRepository.getExportInfo(orderId);
        return tongHopResponses;
    }

    // * -------------------------- Quản lý phê duyệt ------------------------
    //☺ Cập nhật trạng thái khi xuất file csv/ báo lỗi panacim
    public void exportCsvStatus(LenhSanXuat lenhSanXuat) {
        this.lenhSanXuatRepository.exportCsvStatus(
            lenhSanXuat.getTrangThai(),
            lenhSanXuat.getId()
        );
    }
}
