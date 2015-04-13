import sys
def reflexive(pairs, size):
	for x in pairs:
		if(not (x[0] == x[1])):
			return False
	return True

def symmetric(pairs, size):
	for x in pairs:
		if(x[::-1] not in pairs):
			return False
	return True

def matrix(pairs, size):
	mat = [[False for x in range(size)] for x in range(size)]
	for x in pairs:
		mat[x[0]][x[1]] = True
	return mat
		
		
def transitive(pairs, size):
	mat = matrix(pairs, size)
	n = size
	for k in range(n):
		for i in range(n):
			for j in range(n):
				if((mat[i][k] and mat[k][j]) and not mat[i][j]):
					return False
	return True

def partitions(pairs, size):
	counter = 0;
	used = [False for x in range(len(pairs))]
	mat = matrix(pairs, size)
	for i in range(len(pairs)):
		if(not used[i]):
			counter = counter+1
			for j in range(i+1, len(pairs)):
				if(mat[i][j]):
					used[i] = True
	return counter

def main():
	size = int(raw_input())
	L = list()
	for line in sys.stdin:
		s = line.split()
		L.append((int(s[0]),int(s[1])))
	print transitive(L, size)
		
if __name__ == "__main__":
    main()
