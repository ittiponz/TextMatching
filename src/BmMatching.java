import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class BmMatching {
	
	private List<ProductMatched> matchedList = new ArrayList<ProductMatched>();
	private String keyword;
	private int maxMinKwSpace = 0;
	private int maxFirstKeyPos = 0;
	private int maxKeyword = 0;
	
	
	public BmMatching(String keyword) {
		this.matchedList.clear();
		this.keyword = keyword;
		
		try {
			this.matchedList = this.getProduct(this.keyword);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private List<ProductMatched> getProduct(String keyword) throws FileNotFoundException{
		String fPath = "";
		String strProduct = "";
		
		
		int i=1;

		try {
			File f = new File("product.txt");	
			fPath = f.getAbsolutePath();
			FileInputStream file = new FileInputStream(fPath);
			DataInputStream data = new DataInputStream(file);
			BufferedReader rd = new BufferedReader(new InputStreamReader(data));
		  while ((strProduct = rd.readLine()) != null)   {
			  ProductMatched product = new ProductMatched();
			  
			  product = getMatched(keyword,strProduct);
			  System.out.print("");
			  if(product!=null){
				  product.setId(i);
				  matchedList.add(product);
			  }
			  i++;
		  }
		  
		  matchedList = radixSorting(matchedList);
		file.close();
			
		} catch (Exception e) {
			
		}

		return matchedList;		
		
	}
	
	private ProductMatched getMatched(String keyword,String productName){
		List<SpaceChecking> spaceList = new ArrayList<SpaceChecking>();
		
		int pIndex = 0;
		int cIndex = 0;
		int keyIndex = 0;
		int startIndex = 0;
		int i=0;
		int jump = 0;
		int nKeyword = 0;
		int firstKeyPos = -1;
		int minKwSpace = -1;
		
		StringTokenizer str = new StringTokenizer(keyword);
		String word = "";
		
		//Boyer Moore Algorithm
		ProductMatched productMatched = new ProductMatched();
		maxKeyword = str.countTokens();
		
		while(str.hasMoreTokens()){
			word = str.nextToken();
			if(word.length()<=productName.length()){
				cIndex = word.length()-1;
				pIndex = cIndex;
				startIndex =cIndex;
				while(startIndex<productName.length()){
					pIndex = startIndex;
					cIndex = word.length()-1;
					i = cIndex;
					jump = 0;
					boolean isMatch = true;

					while(i>-1){
						if(word.toLowerCase().charAt(cIndex)==productName.toLowerCase().charAt(pIndex)){
							cIndex--;
							pIndex--;
							if(isMatch==false){
								break;
							}
						}else {
							isMatch = false;
							cIndex--;
							jump ++;
						}
						
						i--;
					}
					if(isMatch){
						nKeyword += 1;
						
						SpaceChecking checking = new SpaceChecking();
						checking.setFirstPos(pIndex +1);
						checking.setLastPos(startIndex);
						checking.setKeyword(word);
						spaceList.add(checking);
						
						if(nKeyword>1){
							minKwSpace = getMinSpace(spaceList);
						} 
						if(minKwSpace> maxMinKwSpace){
							maxMinKwSpace = minKwSpace;
						}
						
						if(keyIndex==0){
							firstKeyPos = pIndex+1;
						} else {
							if(firstKeyPos==-1)
								firstKeyPos = pIndex+1;
						}
						if(firstKeyPos>maxFirstKeyPos){
							maxFirstKeyPos = firstKeyPos;
						}
							
						
						break;
	
					} else {
						startIndex += jump;
					}
					keyIndex += 1;
				}
			}
		}
		if(nKeyword==0){
			return null;
		} else {
			productMatched = new ProductMatched();
			productMatched.setnKeyword(nKeyword);
			productMatched.setProductName(productName);
			productMatched.setMinKwSpace(minKwSpace);
			productMatched.setFisrtKeyPos(firstKeyPos);
		}
		
		return productMatched;
	}
	
	private int getMinSpace(List<SpaceChecking> list){
		int minSpace=10000;
		int space=0;
		int i,j;
		for(i=0;i<list.size();i++){
			for(j=i+1;j<list.size();j++){
				SpaceChecking obj1 = new SpaceChecking();
				SpaceChecking obj2 = new SpaceChecking();
				obj1 = list.get(i);
				obj2 = list.get(j);
				
				if(obj1.getFirstPos()<obj2.getFirstPos()){
					space = obj2.getFirstPos() - obj1.getFirstPos();
					
				} else {
					space = obj1.getFirstPos() - obj2.getFirstPos();
				}
				
				if(space<minSpace)
					minSpace = space;		
			}
		}
		
		return minSpace;
	}
	
	private List<ProductMatched> radixSorting(List<ProductMatched> list){
		String strTemp = "";
		String strOrder = "";
		List<ProductMatched> sortedList = new ArrayList<ProductMatched>();
		sortedList.addAll(list);
		
		int maxKeywordLen;
		int maxFirstKeyPosLen;
		int maxMinKwSpaceLen;
		
		strTemp = "" + this.maxKeyword;
		maxKeywordLen = strTemp.length();
		
		strTemp = "" + this.maxFirstKeyPos;
		maxFirstKeyPosLen = strTemp.length();
		
		strTemp = "" + this.maxMinKwSpace;
		maxMinKwSpaceLen = strTemp.length();
		
		strTemp = "" + this.maxKeyword + this.maxFirstKeyPos + this.maxMinKwSpace;
		
		List<List<ProductMatched>> bucket = new ArrayList<List<ProductMatched>>();
		for(int i=0;i<10;i++){
			List<ProductMatched> tempList = new ArrayList<ProductMatched>();
			bucket.add(tempList);
			
		}
		
		for(int i =strTemp.length()-1;i>=0;i--){
			
			for(ProductMatched matched:sortedList){
				strOrder = addZero(""+(this.maxKeyword - matched.getnKeyword()),maxKeywordLen);
				strOrder += addZero(""+matched.getFisrtKeyPos(), maxFirstKeyPosLen);
				if(matched.getMinKwSpace()==-1){
					String str = "";
					for(int j=0;j<maxMinKwSpaceLen;j++){
						str += "9";
					}
					strOrder += str;
				}else {
					strOrder += addZero(""+matched.getMinKwSpace(), maxMinKwSpaceLen);
				}
				int bIndex = Integer.parseInt(""+strOrder.charAt(i));

				bucket.get(bIndex).add(matched);
				System.out.print("");

			}
			
			sortedList.clear();
			
			for(int j=0;j<10;j++){
				sortedList.addAll(bucket.get(j));
				bucket.get(j).clear();
			}
			
		}
		System.out.print("");
		
		return sortedList;
	}
	
	private String addZero(String number,int countLen){
		String str = "";
		str = new String(number);
		for(int i=number.length();i<countLen;i++){
			str = "0" + str;

		}
		
		return str;
	}
	
	public void printResult(){
		
		for(int i=0;i<this.matchedList.size();i++){
			
			ProductMatched matched = new ProductMatched();
			matched = matchedList.get(i);
			System.out.print(matched.getId());
			System.out.print(" |" + matched.getProductName());
			System.out.print(" |" + matched.getnKeyword());
			System.out.print(" |" + matched.getFisrtKeyPos());
			System.out.print(" |" + matched.getMinKwSpace());
			System.out.println();
			
		}
		System.out.println(this.matchedList.size() + " product(s) matched");
	}
}
