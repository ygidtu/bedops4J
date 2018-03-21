package func;
import basic.BasicBed;
import basic.BedFormatError;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.omg.CORBA.BAD_CONTEXT;
import org.omg.CORBA.MARSHAL;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;


public class ClosestFeatures {
    private SetBasicBed setter = new SetBasicBed();

    private String getMessage(BasicBed firstBed, BasicBed[] secondBed, boolean dist, boolean center) {
        String msg = firstBed.getBed();
        for(BasicBed b: secondBed) {
            if (b == null) {
                if (dist) {
                    msg += "|NA|NA";
                } else {
                    msg += "|NA";
                }
                continue;
            }
            if(dist) {
                if (center) {
                    msg += String.format(
                            "|%s|%f",
                            b.getBed(),
                            firstBed.distanceToCenter(b)
                    );
                } else {
                    msg += String.format(
                            "|%s|%d",
                            b.getBed(),
                            firstBed.distanceTo(b)
                    );
                }
            } else {
                msg += String.format(
                        "|%s",
                        b.getBed()
                );
            }
        }

        return msg;
    }


    private String getMessage(HashMap<BasicBed,BasicBed> matches, HashMap<BasicBed, Float> matchDist, BasicBed key, boolean distance, boolean center) {
        String msg = null;
        if( matches.containsKey(key) ) {
            if(distance) {
                if(center) {
                    msg = String.format("%s|%s|%f", key.getBed(), matches.get(key).getBed(), matchDist.get(key));
                }else{
                    msg = String.format("%s|%s|%d", key.getBed(), matches.get(key).getBed(), matchDist.get(key).intValue());
                }
            } else {
                msg = String.format("%s|%s", key.getBed(), matches.get(key).getBed());
            }
        }
        return msg;
    }


    public void closestFeatures(String first, String second, boolean dist, boolean center) throws IOException, BedFormatError {
        this.closestFeatures(first, second, null, dist, center);
    }


    /**
     * Only print the closest one
     * @param first
     * @param second
     * @param output
     */
    public void closestFeatures(String first, String second,  String output, boolean dist, boolean center) throws IOException, BedFormatError {
        HashMap<BasicBed,BasicBed> matches = new HashMap<>();       // for the closest matches
        HashMap<BasicBed, Float> matchDist = new HashMap<>();
        // create print writer
        PrintWriter writer;
        if(output == null) {

            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }
        String firstLine = null, secondLine = null;
        long pointer = 0;
        boolean bIsOverlapped = false;  // this one is used to log if there any overlap happened
        Float distance = (float)0;

        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();


        BasicBed firstBed = setter.setBasicBed(firstLine);
        BasicBed secondBed = setter.setBasicBed(secondLine);


        while (true) {

            int overlap = firstBed.isOverlapped(secondBed);

            if (center) {  // calculate the distance here,这个不如all麻烦，直接在这里计算距离就好
                distance = firstBed.distanceToCenter(secondBed);
            } else {
                distance = (float) firstBed.distanceTo(secondBed);
            }

            if (overlap > 0) {
                if (!bIsOverlapped) {
                    pointer = secondReader.getFilePointer();
                }

                // 如果两个位点不可能有临近位点，那么这个就只可能是上游最近的位点了
                HashMap<BasicBed, Float> tmp = new HashMap<>();
                tmp.put(secondBed, distance);
                if (matches.containsKey(firstBed)) {
                    if (Math.abs(distance) < Math.abs(matchDist.get(firstBed))) {
                        matches.put(firstBed, secondBed);
                        matchDist.put(firstBed, distance);
                    }
                } else {
                    matches.put(firstBed, secondBed);
                    matchDist.put(firstBed, distance);
                }


                secondLine = secondReader.readLine();
                secondBed = setter.setBasicBed(secondLine);

            } else if (overlap < 0) {

                /* 如果没有重合过，那么目前这个就是最近的下游，跟上游比对一下，谁近，保留谁
                 */
                if (!bIsOverlapped) {
                    HashMap<BasicBed, Float> tmp = new HashMap<>();
                    tmp.put(secondBed, distance);
                    if (matches.containsKey(firstBed)) {
                        if (Math.abs(distance) < Math.abs(matchDist.get(firstBed))) {
                            matches.put(firstBed, secondBed);
                            matchDist.put(firstBed, distance);
                        }
                    } else {
                        matches.put(firstBed, secondBed);
                        matchDist.put(firstBed, distance);
                    }
                }

                // 输出结果，清空字典，重置指针
                writer.println(this.getMessage(matches, matchDist, firstBed,dist, center));
                matches.clear();
                matchDist.clear();

                secondReader.seek(pointer);
                secondLine = secondReader.readLine();
                secondBed = setter.setBasicBed(secondLine);

                firstLine = firstReader.readLine();
                firstBed = setter.setBasicBed(firstLine);
            } else {  // two region have overlap

                if (!bIsOverlapped) {
                    bIsOverlapped = true;
                }

                // 此处，有重合之后，选择最后一个有重合的
                HashMap<BasicBed, Float> tmp = new HashMap<>();
                tmp.put(secondBed, distance);
                if (matches.containsKey(firstBed)) {
                    if (Math.abs(distance) <= Math.abs(matchDist.get(firstBed))) {
                        matches.put(firstBed, secondBed);
                        matchDist.put(firstBed, distance);
                    }
                } else {
                    matches.put(firstBed, secondBed);
                    matchDist.put(firstBed, distance);
                }

                // while循环不通过<=就无法读取完整的内容，但是=后就容易读过了，因此特地加一个判断
                if(secondReader.getFilePointer() < secondReader.length()) {
                    secondLine = secondReader.readLine();
                    secondBed = setter.setBasicBed(secondLine);
                } else {
                    break;
                }
            }
        }

        // 额外输出一个结果，并且清空字典
        writer.println(this.getMessage(matches, matchDist, firstBed,dist, center));
        matches.clear();
        matchDist.clear();

        writer.close();
        firstReader.close();
        secondReader.close();

    }



    public void closestFeaturesAll(String first, String second, boolean dist, boolean center) throws IOException, BedFormatError {
        this.closestFeaturesAll(first, second, null, dist, center);
    }

    /**
     * get the closest site, from upstream and downstream at same time
     * the algorithm is kind of messy
     * @param first: path to first file
     * @param second: path to second file
     * @param dist: true: print the distance between two region
     * @param center: true: print the distance between two region center
     * @param output: path to output file, if null, redirect to stdout
     *
     *  Coding: utf-8
     *  讲起来比较麻烦。
     *
     *  需要用一个isOverlapped记录两个位点之间是否 曾经 存在任何重合位点
     *  基础思路：分别读取两个文件，第二个文件读取到哪了，用指针来记录，
     *          基本判断：
     *              第一次有重合位点，那这就是上游最近的，记录第二个的指针
     *              如果在有重合位点的记录之后，第一次没有重合位点了，那么这个就是最近的下游位点，
     *              把第二个文件的指针恢复到第一次有记录的时候，方便进行完整遍历
     *
     *  但是在这个算法里，是不够的
     *  因为，他不止要有重合位点，还要找出非重合最近的那些位点，
     *  那么如果一直没有重合位点，isOverlapped就一致为false，第二个文件的指针就会一直刷新，没法重置会初始状态
     *
     *  所以，加入了第二个变量，numberOfOverlap,来辅助进行判断
     *  numberOfOverlap只在刷新指针或者确实有重合位点进行出输出后才会重置为0
     *  光isOverlapped表明没有重合位点是不够进行指针更新的，numberOfOverlap得不为0才行，表明你们之间曾经有过重合位点
     */
    public void closestFeaturesAll(String first, String second,  String output, boolean dist, boolean center) throws IOException, BedFormatError {
        BasicBed[] Matched = new BasicBed[2];       // for the closest matches
        // create print writer
        PrintWriter writer;
        if(output == null) {

            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }
        String firstLine = null, secondLine = null;
        long pointer = 0;
        boolean bIsOverlapped = false;  // this one is used to log if there any overlap happened
        int numberOfOverlap = 0;        // log how many times that overlaps happened

        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();

        BasicBed firstBed = setter.setBasicBed(firstLine);
        BasicBed secondBed = setter.setBasicBed(secondLine);

        BasicBed lastMatched = setter.setBasicBed(secondLine);

        while (true) {

            int overlap = firstBed.isOverlapped(secondBed);

            if (overlap > 0) {
                if(!bIsOverlapped && numberOfOverlap != 0) {
                    /*
                      * the IsOverlapped and numberOfOverlap together to determine
                      * whether there are overlapped happened to the region from first file
                      */
                    numberOfOverlap = 0;
                    pointer = secondReader.getFilePointer();
                } else {
                    Matched[0] = setter.setBasicBed(secondLine);
                }

                if(secondReader.getFilePointer() < secondReader.getFilePointer()) {
                    secondLine = secondReader.readLine();
                    secondBed = setter.setBasicBed(secondLine);
                } else {
                    break;
                }

                lastMatched = setter.setBasicBed(secondLine);
            } else if (overlap < 0) {

                if(bIsOverlapped) {
                    bIsOverlapped = false;
                    numberOfOverlap = 0;
                    Matched[1] = lastMatched;
                } else {
                    Matched[1] = secondBed;
                }

                /* if this is the first time of not overlap,
                 * after a overlap situation,
                 * then this is the closest from downstream
                 */
                secondReader.seek(pointer);
                secondLine = secondReader.readLine();
                secondBed = setter.setBasicBed(secondLine);

                if(!Arrays.asList(Matched).subList(0, Matched.length - 1).contains(null)) {
                    writer.println(this.getMessage(firstBed, Matched, dist, center));
                    Matched = new BasicBed[2];
                }

                if(firstReader.getFilePointer() < firstReader.length()) {
                    firstLine = firstReader.readLine();
                    firstBed = setter.setBasicBed(firstLine);
                } else {
                    break;
                }

            } else {  // two region have overlap
                numberOfOverlap ++;
                if(!bIsOverlapped) {
                    bIsOverlapped = true;
                    // if this is the first time of overlap, then this is the closest from upstream
                    Matched[0] = setter.setBasicBed(secondLine);
                }

                if(secondReader.getFilePointer() < secondReader.getFilePointer()) {
                    secondLine = secondReader.readLine();
                    secondBed = setter.setBasicBed(secondLine);
                } else {
                    break;
                }
            }

        }


        if(!Arrays.asList(Matched).subList(0, Matched.length - 1).contains(null)) {
            writer.println(this.getMessage(firstBed, Matched, dist, center));
            Matched = new BasicBed[2];
        }

        writer.close();
        firstReader.close();
        secondReader.close();

    }
}
