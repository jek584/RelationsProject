CXX=g++
CFLAGS=-Wall -std=c++0x -O3

all: clean eq eq2 func onetoone onto reflex ref.sym ref.trans sym sym.trans trans

eq: eq.cpp
	$(CXX) $(CFLAGS) -o eq eq.cpp

eq2: eq2.cpp
	$(CXX) $(CFLAGS) -o eq2 eq2.cpp

func: func.cpp
	$(CXX) $(CFLAGS) -o func func.cpp

onetoone: onetoone.cpp
	$(CXX) $(CFLAGS) -o onetoone onetoone.cpp

onto: onto.cpp
	$(CXX) $(CFLAGS) -o onto onto.cpp

reflex: reflex.cpp
	$(CXX) $(CFLAGS) -o reflex reflex.cpp

ref.sym: ref.sym.cpp
	$(CXX) $(CFLAGS) -o ref.sym ref.sym.cpp

ref.trans: ref.trans.cpp
	$(CXX) $(CFLAGS) -o ref.trans ref.trans.cpp

sym: sym.cpp
	$(CXX) $(CFLAGS) -o sym sym.cpp

sym.trans: sym.trans.cpp
	$(CXX) $(CFLAGS) -o sym.trans sym.trans.cpp

trans: trans.cpp
	$(CXX) $(CFLAGS) -o trans trans.cpp

clean:
	rm -f eq eq2 func onetoone onto reflex ref.sym ref.trans sym sym.trans trans
