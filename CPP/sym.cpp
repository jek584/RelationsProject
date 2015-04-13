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
         bool** relations = new bool*[u];

         // Allocate space for the matrix
         for (int i = 0; i < u; i++) {
            relations[i] = new bool[u]();
         }
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
               // Ignore duplicate data
               if (!relations[y - 1][x - 1]) {
                  relations[y - 1][x - 1] = true;
               }
            }
         }

         // Begin tests
         bool isSymmetric = true;

         // Test symmetric
         for (int i = 0; i < u; i++) {
            for (int j = 0; j < u; j++) {
               if (relations[i][j] && !relations[j][i]) {
                  isSymmetric = false;
               }
            }
         }

         cout << "Is " << (isSymmetric ? "" : "not ") << "symmetric" << endl;

         // Clean up memory
         for (int i = 0; i < u; i++) {
            delete[] relations[i];
         }
         delete[] relations;
      }
      catch (bad_alloc& ba) {
         cerr << "error: unable to allocate memory needed for universe " << u << endl;
         return EXIT_FAILURE;
      }
   }
   return 0;
}
