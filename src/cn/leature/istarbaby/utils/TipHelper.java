package cn.leature.istarbaby.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

public class TipHelper {

	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;

	public static void doVibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	public static void doVibrate(final Activity activity, long[] pattern,
			boolean isRepeat) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}

	public static void playBeepSoundAndVibrate(final Activity activity,
			int resourceId, boolean isVibrate) {
		if (activity == null) {
			return;
		}

		boolean shouldPlayBeep = true;
		boolean shouldVibrate = isVibrate;

		// 注册默认 音频通道
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// 检查当前的 铃音模式
		AudioManager audioService = (AudioManager) activity
				.getSystemService(Context.AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			shouldPlayBeep = false;
		}

		// 初始化MediaPlayer对象
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		// 注册事件。当播放完毕一次后，重新指向流文件的开头，以准备下次播放
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer player) {
						player.seekTo(0);
					}
				});

		// 设定数据源，并准备播放
		AssetFileDescriptor file = activity.getResources().openRawResourceFd(
				resourceId);
		try {
			mediaPlayer.setDataSource(file.getFileDescriptor(),
					file.getStartOffset(), file.getLength());
			file.close();
			mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
			mediaPlayer.prepare();
		} catch (Exception ioe) {
			mediaPlayer = null;
			System.out.print(ioe);
		}

		// 开始播放
		if (shouldPlayBeep && (mediaPlayer != null)) {
			mediaPlayer.start();
		}

		if (shouldVibrate) {
			TipHelper.doVibrate(activity, VIBRATE_DURATION);
		}
	}
}
