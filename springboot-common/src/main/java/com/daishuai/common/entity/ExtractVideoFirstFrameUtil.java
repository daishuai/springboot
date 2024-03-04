package com.daishuai.common.entity;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import org.bytedeco.javacpp.Loader;

import java.io.IOException;
import java.util.Optional;

/** @Author huyi @Date 2021/11/11 11:08 @Description: 提取视频第一帧 */
public class ExtractVideoFirstFrameUtil {

    /**
     * 提取主方法
     *
     * @param path MP4视频路径
     * @param tmpDir 临时目录
     * @return 视频第一帧
     * @throws Exception 异常
     */
    public static String extract(String path, String tmpDir) throws Exception {
        String mp4Path;
        if (path.startsWith("http")) {
            mp4Path = tmpDir + "/" + IdUtil.simpleUUID() + ".mp4";
            HttpUtil.downloadFile(path, mp4Path);
        } else {
            mp4Path = path;
        }
        return ffmpegExtractImage(mp4Path, tmpDir + "/" + IdUtil.simpleUUID() + ".jpg")
                .orElseThrow(() -> new Exception("提取失败"));
    }

    /**
     * 提取视频第一帧图片
     *
     * @param mp4Path 视频地址
     * @param picPath 图片地址
     * @return 提取的图片地址
     */
    public static Optional<String> ffmpegExtractImage(String mp4Path, String picPath) {
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

        ProcessBuilder extractBuilder =
                new ProcessBuilder(
                        ffmpeg, "-i", mp4Path, "-f", "image2", "-ss", "1","-frames:v", "1", picPath);
        try {
            extractBuilder.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        // 返回图片文件路径
        return Optional.of(picPath);
    }

    public static void main(String[] args) throws Exception {
        //    System.out.println(extract("C:\\Users\\huyi\\Desktop\\test1.mp4",
        // "C:\\Users\\huyi\\Desktop"));
        System.out.println(
                extract("https://fire-dev.devdolphin.com/ers-file-server/resource/file/default/2024-02-29/ea2463c1174d4729842dc652fd618622.mp4", "d:\\"));
    }

}
