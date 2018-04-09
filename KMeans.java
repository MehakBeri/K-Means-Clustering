/*** Author :Mehak Beri
The University of Texas at Dallas
*****/


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.Math.*;
 

public class KMeans {
    public static void main(String [] args){
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	try{
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g = kmeansImage.createGraphics();
	g.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	int[] rgb=new int[w*h];
	int count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count++]=kmeansImage.getRGB(i,j);
	    }
	}
	// Call kmeans algorithm: update the rgb values
	kmeans(rgb,k);

	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){
        // step 1: pick k random points as cluster centers
        
        int[] red= new int[rgb.length];
        int[] green = new int[rgb.length];
        int[] blue = new int[rgb.length];
        for(int iter=0; iter<rgb.length; iter++){
            Color c = new Color(rgb[iter]);
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            red[iter]=r;
            green[iter]=g;
            blue[iter]=b;
        }
        int[] kClusters= new int[k];
        int[] chosen_red= new int[k];
        int[] chosen_green = new int[k];
        int[] chosen_blue = new int[k];
        for(int i=0; i<k; i++){
            int val=(int) Math.floor(Math.random() * rgb.length);
            kClusters[i]=rgb[val];
            System.out.println("Initial Chosen cluster "+i+" is: "+kClusters[i]);
            chosen_red[i]=red[val];
            chosen_green[i]=green[val];
            chosen_blue[i]=blue[val];
        }        
        // Assign data instances to closest cluster center; returns an array with index referring to the index of the cluster it choses to belong to.  
        int[] assigned= euclideanDistance(red, green, blue, chosen_red, chosen_green, chosen_blue);
        //Change the cluster center to the average of its assigned points 
        for (int j=0; j<k; j++){
            //for each cluster, gather index of points which belong to that cluster; average those points and calculate new cluster rgbs and write into the chosen_red etc...
            int n=0;
            int sum_r=0;
            int sum_g=0;
            int sum_b=0;
            for(int b=0; b<assigned.length; b++){
                if(assigned[b]==j){
                    n++;
                    sum_r = sum_r+red[b];
                    sum_g = sum_g + green[b];
                    sum_b = sum_b + blue[b];
                    
                }
            }
            if(n!=0)
            {
            chosen_red[j]=sum_r/n;
            chosen_green[j]=sum_g/n;
            chosen_blue[j]=sum_b/n;
            }
        }
        //while loop; stop condition: Stop when no points’ assignments change 
        int[] assigned2 = euclideanDistance(red, green, blue, chosen_red, chosen_green, chosen_blue);
        Boolean changed= false;
        for(int r=0; r<assigned.length; r++){
            if(assigned[r]!=assigned2[r]){
                changed=true;
                break;
            }
        }
        while(changed)
        {
            assigned= euclideanDistance(red, green, blue, chosen_red, chosen_green, chosen_blue);
            //Change the cluster center to the average of its assigned points 
            for (int j=0; j<k; j++){
                //for each cluster, gather index of points which belong to that cluster; average those points and calculate new cluster rgbs and write into the chosen_red etc...
                int n=0;
                int sum_r=0;
                int sum_g=0;
                int sum_b=0;
                for(int b=0; b<assigned.length; b++){
                    if(assigned[b]==j){
                        n++;
                        sum_r = sum_r+red[b];
                        sum_g = sum_g + green[b];
                        sum_b = sum_b + blue[b];

                    }
                }
                chosen_red[j]=sum_r/n;
                chosen_green[j]=sum_g/n;
                chosen_blue[j]=sum_b/n;
            }
            assigned2 = euclideanDistance(red, green, blue, chosen_red, chosen_green, chosen_blue);
            changed= false;
            for(int r=0; r<assigned.length; r++){
                if(assigned[r]!=assigned2[r]){
                    changed=true;
                    break;
                }
            }
        }
        //update rgb values
        for(int y=0; y<kClusters.length; y++){
            kClusters[y] = new Color(chosen_red[y],chosen_green[y],chosen_blue[y]).getRGB();
        }
        for(int u=0; u<assigned2.length; u++){
            int ind= assigned2[u];
            rgb[u]=kClusters[ind];
        }
        
    }
    
    private static int[] euclideanDistance(int[] red, int[] green, int[] blue, int[] chosen_red, int[] chosen_green, int[] chosen_blue){
        int[] distances = new int[red.length];
        int k= chosen_red.length;
        for(int j=0; j<red.length; j++)
        {
            int r_pt=red[j];
            int g_pt=green[j];
            int b_pt=blue[j];
            // for each point - calculate distance wrt each k-mean cluster center; then assign the index number of the k-mean cluster which has the minimum distance
            double[] dist= new double[k];
            for(int i=0; i<k; i++){
                int r_chosen=chosen_red[i];
                int g_chosen=chosen_green[i];
                int b_chosen=chosen_blue[i];
                int r_diff= (r_chosen-r_pt)*(r_chosen-r_pt);
                int g_diff= (g_chosen-g_pt)*(g_chosen-g_pt);
                int b_diff= (b_chosen-b_pt)*(b_chosen-b_pt);
                dist[i]= Math.sqrt(r_diff + g_diff + b_diff);
            }
            int min= FindSmallest(dist);
            distances[j]= min;
        }
        return distances;
    }
    private static int FindSmallest (double[] arr1){
       int index = 0;
       double min = arr1[index];
       for (int i=1; i<arr1.length; i++){

           if (arr1[i] < min ){
               min = arr1[i];
               index = i;
           }
       }
       return index ;
    }
}
