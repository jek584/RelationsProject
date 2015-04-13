/*
 * Author:  Constantine Lopez, constantine2013@my.fit.edu
 * Course:  CSE 4415, Section 01, Spring 2015
 * Project: relations, Relations
 */

#include <iostream>
#include <sstream>
#include <queue>
using namespace std;

// Converts the provided string to a signed 32-bit integer
// Unsigned integer would require a conversion to unsigned long and then a range check
bool strToInt(const string& input, int& value)
{
   try {
      size_t index;
      value = stoi(input, &index, 10);
      return index == input.size();
   } catch (exception&) {
      // cerr << "unable to convert string to integer" << endl;
      return false;
   }
}

string trim(const string& str) {
   if (str.empty()) {
      return str;
   }
   size_t first = str.find_first_not_of(' ');
   size_t last = str.find_last_not_of(' ');
   return str.substr(first, last-first+1);
}

int main(int argc, char* argv[]) {
   string input;
   int u = 0;

   // Get the universe size from stdin
   getline(cin, input);

   // Validate the universe size
   if (!strToInt(trim(input), u) || (u < 1)) {
      cerr << "error: invalid universe size - \"" << input << "\"" << endl;
      return EXIT_FAILURE;
   } else {
      try {
         int* xSet = new int[u]();
         int* ySet = new int[u]();
         string line;

         // Read all relations from stdin
         while (getline(cin, line)) {
            // Validate the line is composed of a pair of integers
            istringstream ss(line);
            string first, second;
            getline(ss, first, ' ');
            getline(ss, second, ' ');

            int x = 0, y = 0;
            if ((!strToInt(trim(first), x) || (x < 1) || (x > u)) || (!strToInt(trim(second), y) || (y < 1) || (y > u))) {
               cerr << "error: invalid relation pair \"" << line << "\"" << endl;
               return EXIT_FAILURE;
            } else {
               // Warning - Duplicate data can affect results
               xSet[x - 1]++;
               ySet[y - 1]++;
            }
         }

         // Begin tests
         bool isOneToOne = true;

         // Test one-to-one
         for (int i = 0; i < u; i++) {
            if ((xSet[i] > 1) || (ySet[i] > 1)) {
               isOneToOne = false;
            }
         }

         cout << "Is " << (isOneToOne ? "" : "not ") << "onetoone" << endl;

         // Clean up memory
         delete[] xSet;
         delete[] ySet;
      }
      catch (bad_alloc& ba) {
         cerr << "error: unable to allocate memory needed for universe " << u << endl;
         return EXIT_FAILURE;
      }
   }
   return 0;
}
