import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Scanner;

public class DataMiningProject {
	
	public static final double THRESHOLD = 0.70;

	private minKValuesClass[] minKValues;
	private Element[] DistancesElementArray;
	private double [][] trainingDataArray;
	private double [][] newInstances;
	
	int TP = 0;
	int FP = 0;
	int TN = 0;
	int FN = 0;
	int k = 0;
	
	double falseAdmit;
	double trueAdmit;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		DataMiningProject project = new DataMiningProject();
		project.start();
		
	}
	
	private void start() {

		init();
		
		newInstances = getNewInstance();
	
		trainingDataArray = setUpCSVArray();
		
		applyKNNAlgorithm();
	}

	private void init() {
		System.out.println("============ Data Mining Project =================");
		trainingDataArray = new double[334][9];
		newInstances = new double[167][9];
		DistancesElementArray = new Element[334];
		minKValues = new minKValuesClass[60];   //Test edildi ve en iyi accuracy, k sayısını 60 seçerek elde edildi.
		for(int i = 0 ; i < minKValues.length ; i ++) {
			minKValues[i] = new minKValuesClass();
			minKValues[i].distance = Integer.MAX_VALUE;
			minKValues[i].admitChance = 0;
		}		
	}

	public double[][] setUpCSVArray() {
		Scanner scanIn = null;
		int Rowc = 0;
		String inputLine = "";
		URL url = getClass().getResource("Admission_Predict_Ver1.1.csv");
		File file = new File(url.getPath());
		String fileLocation = file.getAbsolutePath();	
		try {
			scanIn = new Scanner(new BufferedReader(new FileReader(fileLocation)));
			String headerLine = scanIn.nextLine();
			while(scanIn.hasNextLine()) {
				inputLine = scanIn.nextLine();
				String[] inArray = inputLine.split(",");
				for(int x = 0; x < inArray.length; x++) {
					trainingDataArray[Rowc][x] = Double.parseDouble(inArray[x]);
				}
				Rowc++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return trainingDataArray;			
	}
	
	private double[][] getNewInstance() {
		Scanner sc = null;
		int Rowc = 0;
		String inputLine = "";
		URL url = getClass().getResource("UntitledDocument1.csv");
		File file = new File(url.getPath());
		String fileLocation = file.getAbsolutePath();	
		try {
			sc = new Scanner(new BufferedReader(new FileReader(fileLocation)));
			String headerLine = sc.nextLine();
			while(sc.hasNextLine()) {
				inputLine = sc.nextLine();
				String[] inArray = inputLine.split(",");
				System.out.println(inArray.length);
				System.out.println(newInstances.length);
				for(int x = 0; x < inArray.length; x++) {
					newInstances[Rowc][x] = Double.parseDouble(inArray[x]);
				}
				Rowc++;
			}		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newInstances;
	}

	private void applyKNNAlgorithm() {
		for(k = 0 ; k < newInstances.length ; k++) {
			System.out.print(" instance is : ");
			for(int l = 0; l < 9 ; l++)
			System.out.print(" " + newInstances[k][l] + " , ");
			System.out.println("");
			calcDistances();
		}
		calcAccuracy();
		calcFMeasure(calcPrecision(), calcRecall());
	}
	
	public void calcDistances() {
		for(int i = 0 ; i < trainingDataArray.length ; i++){
			Element element = new Element();
			element.id = i;
			for(int j = 1 ; j < 8 ; j++) {
				element.distance += Math.pow((newInstances[k][j]-trainingDataArray[i][j]), 2);
			}
			element.distance = Math.sqrt(element.distance);
			DistancesElementArray[i] = element;
		}		
		
		DistancesElementArray = sort(DistancesElementArray, 0, DistancesElementArray.length-1 );
		
		initMinKValues(DistancesElementArray);
	}
	
	private void initMinKValues(Element[] distancesElementArray) {
		for(int i = 0 ; i < minKValues.length ; i ++) {
			minKValues[i].distance = distancesElementArray[i].distance;
			minKValues[i].admitChance = trainingDataArray[distancesElementArray[i].id][8];
		}
		guessTheClass(minKValues);
	}

	private void guessTheClass(minKValuesClass[] minKValues) {
		falseAdmit = 0;
		trueAdmit = 0;
		for(int i = 0 ; i < minKValues.length ; i ++) {
			if(minKValues[i].admitChance >= THRESHOLD) {
				trueAdmit += 1/Math.pow(minKValues[i].distance, 2);
			}else {
				falseAdmit += 1/Math.pow(minKValues[i].distance, 2);
			}
		}
		setAccuracyPieces();
	}

	private void setAccuracyPieces() {
		if(trueAdmit >= falseAdmit) {
			System.out.println(k + "Class of instance is YES");
			if(newInstances[k][8] >= THRESHOLD) {
				TP++;
			}else {
				FP++;
			}
		}
		else {
			System.out.println(k + "Class of instance is NO");
			if(newInstances[k][8] >= THRESHOLD) {
				FN++;
			}else {
				TN++;
			}
		}	
		System.out.println("-------------------------------------------------------------------------------------------");
	}

	private void calcAccuracy() {
		System.out.println("TP = " + TP );
		System.out.println("FP = " + FP );
		System.out.println("TN = " + TN );
		System.out.println("FN = " + FN );
		int sum = TP + TN + FP + FN;
		int a = TP + TN;
		System.out.println("Test Data Count = " + sum);
		double acc;
		acc = ((double)(a)/(double)(sum));
		System.out.println("Accuracy = " + acc);
	}

	private double calcPrecision() {
		double sum = TP + FP;
		double prec = TP / sum;
		return prec;		
	}

	private double calcRecall() {
		double sum = TP + FN;
		double rec = TP / sum;
		return rec;		
	}

	
	private void calcFMeasure(double P, double R) {
		System.out.println("Precision = " + P);
		System.out.println("Recall = " + R);
		System.out.println("F_Measure = " + ((2 * P * R) / (P + R)));
	}
	
	Element[] sort(Element arr[], int l, int r) 
    {
        if (l < r) 
        { 
            // Find the middle point 
            int m = (l+r)/2; 
  
            // Sort first and second halves 
            sort(arr, l, m); 
            sort(arr , m+1, r); 
  
            // Merge the sorted halves 
            merge(arr, l, m, r); 
            return arr;
        } 
        return arr;
    } 
		
	void merge(Element arr[], int l, int m, int r) 
    { 
        // Find sizes of two subarrays to be merged 
        int n1 = m - l + 1; 
        int n2 = r - m; 
  
        /* Create temp arrays */
        Element L[] = new Element [n1]; 
        Element R[] = new Element [n2]; 
  
        /*Copy data to temp arrays*/
        for (int i=0; i<n1; ++i) 
            L[i] = arr[l + i]; 
        for (int j=0; j<n2; ++j) 
            R[j] = arr[m + 1+ j]; 
  
  
        /* Merge the temp arrays */
  
        // Initial indexes of first and second subarrays 
        int i = 0, j = 0; 
  
        // Initial index of merged subarry array 
        int k = l; 
        while (i < n1 && j < n2) 
        { 
            if (L[i].distance <= R[j].distance) 
            { 
                arr[k] = L[i]; 
                i++; 
            } 
            else
            { 
                arr[k] = R[j]; 
                j++; 
            } 
            k++; 
        } 
  
        /* Copy remaining elements of L[] if any */
        while (i < n1) 
        { 
            arr[k] = L[i]; 
            i++; 
            k++; 
        } 
  
        /* Copy remaining elements of R[] if any */
        while (j < n2) 
        { 
            arr[k] = R[j]; 
            j++; 
            k++; 
        } 
    } 
      
	
}
