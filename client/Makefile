EXE := LoupGarou_Client.jar
MAIN_CLASS := org.garou.LoupGarou

BIN := bin
SRC := src

$(BIN):
	mkdir $(BIN)

clean:
	rm -rf $(BIN) $(EXE)

compile: $(BIN)
	javac -encoding utf-8 -d $(BIN) $$(find $(SRC)/* -type f -name "*.java")

$(EXE): compile
	jar cef $(MAIN_CLASS) $(EXE) assets/ -C $(BIN)/ .

build: $(EXE)

run: $(EXE)
	java -jar $(EXE)

1: kill $(EXE)
	java -jar $(EXE) &

2: kill $(EXE)
	for i in $$(seq 1 2) ; do (java -jar $(EXE) &) ; done

3: kill $(EXE)
	for i in $$(seq 1 3) ; do (java -jar $(EXE) &) ; done

4: kill $(EXE)
	for i in $$(seq 1 4) ; do (java -jar $(EXE) &) ; done

5: kill $(EXE)
	for i in $$(seq 1 5) ; do (java -jar $(EXE) &) ; done

6: kill $(EXE)
	for i in $$(seq 1 6) ; do (java -jar $(EXE) &) ; done

7: kill $(EXE)
	for i in $$(seq 1 7) ; do (java -jar $(EXE) &) ; done

8: kill $(EXE)
	for i in $$(seq 1 8) ; do (java -jar $(EXE) &) ; done

kill:
	-kill -9 $$(ps | grep java | awk '{print $$1;}' | tr '\n' ' ') 2> /dev/null || true

.PHONY: clean compile build run 1 2 3 4 5 6 7 8 kill

