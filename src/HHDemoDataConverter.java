import java.io.*;

public class HHDemoDataConverter{
	// HHdemo(論文のデモアプリ)のデータを変換するプログラム
	// アプリで記録したデータを、ライブラリとして使えるようにする
	//
	static final String FILE_PATH = "C:\\Users\\KAZUYA\\Desktop\\hhdemoRelease\\binary\\windows\\x64\\Release\\onizawa_1.txt";
	static final int[] ADD_ELEMENT = {9,10,11,13,14,15,16}; //元ファイルの各ストロークのうち、変換後ファイルに追加する情報(ここに書かれた順番で追加される)
	static final boolean reverseY = true; //Y座標を判定させるかどうか(反転させないと、上下逆に出力される)
	static final boolean ONLY_XY = true; //XY座標とタイムスタンプ以外0にする場合true
	static final boolean[] XY_ELEMENT = {true,true,true,false,false,false,false};
	static final int NUM_OF_ELEMENT = ADD_ELEMENT.length;

	public static void main(String[] args){
		ConvertRecordedData2LibraryData(FILE_PATH);
	}

	static void ConvertRecordedData2LibraryData(String path){
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));

			String str = br.readLine();
			int stroke = 0;//ストローク数カウント用
			StringBuilder buf = new StringBuilder();//出力を一時保存

			while(str!=null){
				//ストロークだったら
				if (str.contains("f f f f f f f f")){

					String[] l = str.split(" ");
					int sample_i = 0;
					StringBuilder tmp = new StringBuilder();

					while(l[20].equals("0x00")){//0x00だとストロークの途中　0x78だと最後の点

						tmp.append(addSpace(Integer.toString(sample_i), 5));
						for(int i = 0; i<NUM_OF_ELEMENT; i++){
							if (ONLY_XY && !XY_ELEMENT[i]){
								tmp.append(addSpace("0",14));
							}else{
								if (reverseY && i==2){
									double y =Double.parseDouble(l[ADD_ELEMENT[i]]);
									l[ADD_ELEMENT[i]] = Double.toString(1-y);
								}
								tmp.append(addSpace(l[ADD_ELEMENT[i]],14));
							}
						}
						tmp.append("\n");

						sample_i++;
						l = br.readLine().split(" ");
						if (l.length < 20) break;
					}

					buf.append("stroke ");
					buf.append(stroke);
					buf.append("\n");
					buf.append(sample_i);
					buf.append("\n");
					buf.append(tmp);
					buf.append("\n");

					stroke++;
				}

				str = br.readLine();
			}
			br.close();

			PrintWriter pw = new PrintWriter(new FileWriter(path+".cyn"));
			pw.print(LibraryHeader());
			pw.println(stroke);
			pw.print(buf);
			pw.close();

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//strの文字数がwidthになるように、str（最大10文字）の後にスペースを追加する
	static StringBuilder addSpace(String str, int width){

		int len = str.length();
		if (len>9) len = 9;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i<width; i++){
			if (i<len){
				sb.append(str.charAt(i));
			}else{
				sb.append(" ");
			}
		}

		return sb;
	}

	static String LibraryHeader(){
		return "#\nVersion 0.0\n#\n\nphotoshop 5\n1\nunrelated\n\n";
	}

}