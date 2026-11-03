/** Âm báo kết quả scan (dùng chung 2 file âm thanh với Cập nhật vật tư) */
export function playScanSound(ok: boolean): void {
  try {
    const audio = new Audio(
      ok
        ? "content/images/successed-295058.mp3"
        : "content/images/beep_warning.mp3",
    );
    void audio.play().catch(() => undefined);
  } catch {
    // trình duyệt không phát được âm thanh
  }
}
