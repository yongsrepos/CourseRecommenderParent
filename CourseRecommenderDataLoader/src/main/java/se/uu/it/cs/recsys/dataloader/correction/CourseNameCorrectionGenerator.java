
package se.uu.it.cs.recsys.dataloader.correction;

/*
 * #%L
 * CourseRecommenderDataLoader
 * %%
 * Copyright (C) 2015 Yong Huang  <yong.e.huang@gmail.com >
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.common.collect.Maps;
import java.util.Map;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
class CourseNameCorrectionGenerator {

    public static Map<String, String> getWrongToCorrectNamePairs() {
        Map<String, String> wrongToCorrect = Maps.newHashMap();

        wrongToCorrect.put("Algorithms and Data Structures 111", "Algorithms and Data Structures III");
        wrongToCorrect.put("Operating Systems 1", "Operating Systems I");
        wrongToCorrect.put("Data Mining .", "Data Mining");
        wrongToCorrect.put("Data Mining ,", "Data Mining");
        wrongToCorrect.put("Database Design 1", "Database Design I");
        wrongToCorrect.put("Database Design !", "Database Design I");
        wrongToCorrect.put("Database Design 1 ,", "Database Design I");
        wrongToCorrect.put("Compiler Design 1", "Compiler Design I");
        wrongToCorrect.put("Project CS .", "Project CS");
        wrongToCorrect.put("Computer Networks 1", "Computer Networks I");
        wrongToCorrect.put("Computer Networks!", "Computer Networks I");
        wrongToCorrect.put("Test [Methodology", "Test Methodology");
        wrongToCorrect.put("Real Time Systems 1", "Real Time Systems I");
        wrongToCorrect.put("Rea! Time Systems 1", "Real Time Systems I");
        wrongToCorrect.put("User Interface Programming 1", "User Interface Programming I");
        wrongToCorrect.put("User Interface Programming ]l", "User Interface Programming II");
        wrongToCorrect.put("User Interface Programming l(", "User Interface Programming II");
        wrongToCorrect.put("User Interface Programming 11", "User Interface Programming II");
        wrongToCorrect.put("User Interface Programming!!", "User Interface Programming II");
        wrongToCorrect.put("High Performance Computing.and Programming", "High Performance Computing and Programming");
        wrongToCorrect.put("Methods of programming DV2 ?", "Methods of programming DV2");
        wrongToCorrect.put("Database Design 11", "Database Design II");
        wrongToCorrect.put("Database Design I!", "Database Design II");
        wrongToCorrect.put("Database Design li", "Database Design II");
        wrongToCorrect.put("Database Design I1", "Database Design II");
        wrongToCorrect.put("Compiler Design 11", "Compiler Design II");
        wrongToCorrect.put("Compiler Design I1", "Compiler Design II");
        wrongToCorrect.put("Database Design I ,", "Database Design I");
        wrongToCorrect.put("Secure Computer Systems 1", "Secure Computer Systems I");
        wrongToCorrect.put("Secure Computer Systems 1!", "Secure Computer Systems II");
        wrongToCorrect.put("Secure Computer Systems 11", "Secure Computer Systems II");
        wrongToCorrect.put("Secure Computer Systems I!","Secure Computer Systems II");
        wrongToCorrect.put("Secure Computer Systems I1", "Secure Computer Systems II");
        wrongToCorrect.put("Data Mining 1", "Data Mining I");
        wrongToCorrect.put("Data Mining i", "Data Mining I");
        wrongToCorrect.put("Advanced Algorlthmics", "Advanced Algorithmics");
        wrongToCorrect.put("Computer Networks 11", "Computer Networks II");
        wrongToCorrect.put("Computer Networks I1", "Computer Networks II");
        wrongToCorrect.put("3rogramming of Parallel Computers", "Programming of Parallel Computers");
        wrongToCorrect.put("Algorithms and Data Structures 11", "Algorithms and Data Structures II");
        wrongToCorrect.put("Algorithms and Data Structures li", "Algorithms and Data Structures II");
        wrongToCorrect.put("Algorithms and Data Structures K", "Algorithms and Data Structures II");
        wrongToCorrect.put("Algorithms and Data Structures 1)", "Algorithms and Data Structures II");
        wrongToCorrect.put("Algorithms and Data Structures 1!", "Algorithms and Data Structures II");
        wrongToCorrect.put("Computer Networks 111", "Computer Networks III");
        wrongToCorrect.put("Computer Networks I11", "Computer Networks III");
        wrongToCorrect.put("Computer Networks II1", "Computer Networks III");
        wrongToCorrect.put("Functional Programming I .", "Functional Programming I");
        wrongToCorrect.put("Rea! Time Systems", "Real Time Systems");
        wrongToCorrect.put("Real Time Systems IV1N1", "Real Time Systems MN1");
        wrongToCorrect.put("Real Time Systems MNl", "Real Time Systems MN1");
        wrongToCorrect.put("Artificial intelligence", "Artificial Intelligence");
        wrongToCorrect.put("Data Mining 11", "Data Mining II");
        wrongToCorrect.put("Data Mining 1!", "Data Mining II");
        wrongToCorrect.put("Data Mining If", "Data Mining II");
        wrongToCorrect.put("Computer Networks 1", "Computer Networks I");
        wrongToCorrect.put("Computer Networks!", "Computer Networks I");
        wrongToCorrect.put("Computer Networks 11", "Computer Networks II");
        wrongToCorrect.put("Computer Networks [[", "Computer Networks II");
        wrongToCorrect.put("Computer Architecture II ?.", "Computer Architecture II");
        wrongToCorrect.put("Computer Architecture 11", "Computer Architecture II");
        wrongToCorrect.put("Algorithms and Data Structures 1", "Algorithms and Data Structures I");
        wrongToCorrect.put("Functional Programming 1", "Functional Programming I");
        wrongToCorrect.put("Functional Programming 1 .", "Functional Programming I");
        wrongToCorrect.put("Functional Programming!", "Functional Programming I");
        wrongToCorrect.put("Computer Assisted Image Analysis 11", "Computer Assisted Image Analysis II");
        wrongToCorrect.put("Scientific Computing 1", "Scientific Computing I");
        wrongToCorrect.put("Computer Games Development 1", "Computer Games Development I");
        wrongToCorrect.put("Computer Games Development!", "Computer Games Development I");
        wrongToCorrect.put("Degree Project H in Computer Science", "Degree Project E in Computer Science");
        wrongToCorrect.put("DegreekPro]ect E in Computer Science", "Degree Project E in Computer Science");
        wrongToCorrect.put("Degree Project E in Computer Science .", "Degree Project E in Computer Science");
        wrongToCorrect.put("Advanced Computer Architecture ?", "Advanced Computer Architecture");
        wrongToCorrect.put("Computer Assisted Image Analysis 1", "Computer Assisted Image Analysis I");
        wrongToCorrect.put("Computer Assisted [mage Analysis 1", "Computer Assisted Image Analysis I");
        wrongToCorrect.put("Computing Education Research '", "Computing Education Research");
        wrongToCorrect.put("Compiler Design 1", "Compiler Design I");
        wrongToCorrect.put("Compiler Design!", "Compiler Design I");
        wrongToCorrect.put("Compiler Design 1 -", "Compiler Design I");
        wrongToCorrect.put("Compiler Design I -", "Compiler Design I");
        wrongToCorrect.put("Advanced Computer Science Studies In Sweden", "Advanced Computer Science Studies in Sweden");
        wrongToCorrect.put("ProgrammingTheory", "Programming Theory");

        return wrongToCorrect;
    }

}
