# Advent of Code 2016, Day 3: Squares With Three Sides

## Part 1: Row-by-Row Triangles

The first part of the problem asks us to count the number of valid triangles from a list of side lengths. A triangle is valid if the sum of any two sides is greater than the third side. The input provides three side lengths per line.

Our approach is to parse each line into a list of integers, representing the sides of a potential triangle. We then use a helper function to check if these sides can form a valid triangle.

## Part 2: Column-by-Column Triangles

The second part is similar, but with a twist: the triangles are specified in columns. Three vertically adjacent numbers now form the sides of a triangle.

To solve this, we first parse all the numbers from the input into a single list. We then group the numbers into chunks of three, representing the rows of the input. We then 'transpose' this data structure, so that the columns become rows. Finally, we check each of the new rows to see if it forms a valid triangle.