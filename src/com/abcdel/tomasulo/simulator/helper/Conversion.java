package com.abcdel.tomasulo.simulator.helper;

import com.abcdel.tomasulo.simulator.instruction.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Conversion {

  public static List<String> toBinary(String filename) {
    return toBinary(readFromFile(filename));
  }

  public static void toBinary(String filenameRead, String filenameWrite) {
    writeToFile(filenameWrite, toBinary(readFromFile(filenameRead)));
  }

  public static void toBinary( String filenameWrite, List<String> lines) {
    writeToFile(filenameWrite, toBinary(lines));
  }

  public static List<String> toBinary(List<String> readableInstructions){
    List<String> instructionsBinary = new ArrayList<>();
    HashMap<String, Integer> labels = new HashMap<>();

    for(int i = 0; i < readableInstructions.size(); i++){
      if(readableInstructions.get(i).contains(":")){
        String label = readableInstructions.get(i).split(":")[0];
        String instr = readableInstructions.get(i).split(":")[1];
        label = label.trim().toLowerCase();
        labels.put(label, i * 4);
        readableInstructions.set(i, instr);
      }
    }

    for(String line : readableInstructions){
      instructionsBinary.add(toBinaryLine(line, labels));
    }

    return instructionsBinary;
  }

  public static List<Instruction> toReadableInstruction(String filename){
    return toReadableInstruction(readFromFile(filename));
  }

  public static List<Instruction> toReadableInstruction(List<String> binaryInstructions) {
    List<Instruction> instructions = new ArrayList<>();

    for(String line : binaryInstructions){
      instructions.add(toReadableInstructionLine(line));
    }

    return instructions;
  }

  public static List<String> toReadableInstructionString(String filename) {
    return toReadableInstructionString(readFromFile(filename));
  }

  public static void toReadableInstructionString(String filenameRead, String filenameWrite) {
    writeToFile(filenameWrite, toReadableInstructionString(readFromFile(filenameRead)));
  }

  public static void toReadableInstructionString( String filenameWrite, List<String> lines) {
    writeToFile(filenameWrite, toReadableInstructionString(lines));
  }

  public static List<String> toReadableInstructionString(List<String> binaryInstructions){
    List<String> readableInstructions = new ArrayList<>();

    for(String line : binaryInstructions){
      readableInstructions.add(toReadableInstructionLineString(line));
    }

    return readableInstructions;
  }

  private static String toRegisterName(String registerBinary){
    int registerNumber = Integer.parseInt(registerBinary, 2);
    return "r" + registerNumber;
  }


  public static Instruction toReadableInstructionLine(String line){
    line = line.trim();
    if(line.contains(";")){
      line = line.substring(0, line.indexOf(';'));
    }
    line = line.trim();

    Instruction instruction = null;
    String operation = line.substring(0, 6);
    String funct;
    String rs;
    String rt;
    String rd;
    String immediate;

    switch(operation){
      case "000000":
        funct = line.substring(26);
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        rd = line.substring(16, 21);
        switch(funct){
          case "100000": //add
            instruction = new Add(Integer.parseInt(rd, 2),
                    Integer.parseInt(rs, 2),
                    Integer.parseInt(rt, 2));
            break;
          case "100010": //sub
            instruction = new Sub(Integer.parseInt(rd, 2),
                    Integer.parseInt(rs, 2),
                    Integer.parseInt(rt, 2));
            break;
          case "011000": //mul
            instruction = new Mul(Integer.parseInt(rd, 2),
                    Integer.parseInt(rs, 2),
                    Integer.parseInt(rt, 2));
            break;
          case "000000": //nop
            instruction = new Nop();
            break;
        }
        break;
      case "001000": //addi
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        instruction = new Addi(Integer.parseInt(rt, 2),
                Integer.parseInt(rs, 2),
                Integer.parseInt(immediate, 2));
        break;
      case "000101": //beq
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        instruction = new Beq(Integer.parseInt(rt, 2),
                Integer.parseInt(rs, 2),
                Integer.parseInt(immediate, 2));
        break;
      case "000111": //ble
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        instruction = new Ble(Integer.parseInt(rt, 2),
                Integer.parseInt(rs, 2),
                Integer.parseInt(immediate, 2));
        break;
      case "000100": //bne
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        instruction = new Bne(Integer.parseInt(rt, 2),
                Integer.parseInt(rs, 2),
                Integer.parseInt(immediate, 2));
        break;
      case "000010": //jmp
        immediate = line.substring(6);
        instruction = new Jmp(Integer.parseInt(immediate, 2));
        break;
      case "100011": //lw
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        instruction = new Lw(Integer.parseInt(rt, 2),
                Integer.parseInt(rs, 2),
                Integer.parseInt(immediate, 2));
        break;
      case "101011": //sw
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        instruction = new Sw(Integer.parseInt(rt, 2),
                Integer.parseInt(rs, 2),
                Integer.parseInt(immediate, 2));
        break;
    }

    return instruction;
  }


  private static String toReadableInstructionLineString(String line){
    line = line.trim();
    if(line.contains(";")){
      line = line.substring(0, line.indexOf(';'));
    }
    line = line.trim();

    String readableInstruction = "";
    String operation = line.substring(0, 6);
    String funct;
    String rs;
    String rt;
    String rd;
    String immediate;

    switch(operation){
      case "000000":
        funct = line.substring(26);
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        rd = line.substring(16, 21);
        switch(funct){
          case "100000": //add
            readableInstruction = "add " +
                    toRegisterName(rd) + "," +
                    toRegisterName(rs) + "," +
                    toRegisterName(rt);
            break;
          case "100010": //sub
            readableInstruction = "sub " +
                    toRegisterName(rd) + "," +
                    toRegisterName(rs) + "," +
                    toRegisterName(rt);
            break;
          case "011000": //mul
            readableInstruction = "mul " +
                    toRegisterName(rd) + "," +
                    toRegisterName(rs) + "," +
                    toRegisterName(rt);
            break;
          case "000000": //nop
            readableInstruction = "nop";
            break;
        }
        break;
      case "001000": //addi
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        readableInstruction = "addi " +
                toRegisterName(rt) + "," +
                toRegisterName(rs) + "," +
                Integer.parseInt(immediate, 2);
        break;
      case "000101": //beq
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        readableInstruction = "beq " +
                toRegisterName(rs) + "," +
                toRegisterName(rt) + "," +
                Integer.parseInt(immediate, 2);
        break;
      case "000111": //ble
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        readableInstruction = "ble " +
                toRegisterName(rs) + "," +
                toRegisterName(rt) + "," +
                Integer.parseInt(immediate, 2);
        break;
      case "000100": //bne
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        readableInstruction = "bne " +
                toRegisterName(rs) + "," +
                toRegisterName(rt) + "," +
                Integer.parseInt(immediate, 2);
        break;
      case "000010": //jmp
        immediate = line.substring(6);
        readableInstruction = "jmp " +
                Integer.parseInt(immediate, 2);
        break;
      case "100011": //lw
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        readableInstruction = "lw " +
                toRegisterName(rt) + "," +
                Integer.parseInt(immediate, 2) + "(" +
                toRegisterName(rs) + ")";
        break;
      case "101011": //sw
        rs = line.substring(6, 11);
        rt = line.substring(11, 16);
        immediate = line.substring(16);
        readableInstruction = "sw " +
                toRegisterName(rt) + "," +
                Integer.parseInt(immediate, 2) + "(" +
                toRegisterName(rs) + ")";
        break;
    }

    return readableInstruction;
  }

  private static String getRegisterBinary(String register){
    String registerBinary = Integer.toBinaryString(Integer.parseInt(register.replace("r", "")));
    String zeros = "";
    for(int i = 0; i <  5 - registerBinary.length(); i++){
      zeros = zeros + "0";
    }
    return zeros + registerBinary;
  }

  private static String getImmediateBinary(String immediate, int immediateSize){
    String immediateBinary = Integer.toBinaryString(Integer.parseInt(immediate));
    String zeros = "";
    for(int i = 0; i <  immediateSize - immediateBinary.length(); i++){
      zeros = zeros + "0";
    }
    return zeros + immediateBinary;
  }

  private static String instructionInRType(String operation, String funct, String rs, String rt, String rd){
    String line = operation +
                  getRegisterBinary(rs) +
                  getRegisterBinary(rt) +
                  getRegisterBinary(rd) +
                  "00000" + funct;
    return line;
  }

  private static String instructionInIType(String operation, String rs, String rt, String immediate){
    String line = operation +
            getRegisterBinary(rs) +
            getRegisterBinary(rt) +
            getImmediateBinary(immediate, 16);
    return line;
  }

  private static String instructionLoadStore(String operation, String rt, String toSplit){
    String immediate;
    String rs;

    immediate = (toSplit.split("\\("))[0];
    rs = (toSplit.split("\\("))[1];
    immediate = immediate.trim();
    rs = rs.replace(")", "");
    rs = rs.trim();

    String line = operation +
            getRegisterBinary(rs) +
            getRegisterBinary(rt) +
            getImmediateBinary(immediate, 16);
    return line;
  }

  private static String instructionInJType(String operation, String immediate){
    String line = operation +
            getImmediateBinary(immediate, 26);
    return line;
  }

  public static String toBinaryLine(String line, HashMap<String, Integer> labels){
    List<String> instructionParts = getInstructionParts(line, labels);
    String binaryLine = "";

    switch(instructionParts.get(0)){
      case "add":
        binaryLine = instructionInRType(
                "000000",
                "100000",
                instructionParts.get(2),
                instructionParts.get(3),
                instructionParts.get(1));
        break;
      case "addi":
        binaryLine = instructionInIType(
                "001000",
                instructionParts.get(2),
                instructionParts.get(1),
                instructionParts.get(3));
        break;
      case "beq":
        binaryLine = instructionInIType(
                "000101",
                instructionParts.get(1),
                instructionParts.get(2),
                instructionParts.get(3));
        break;
      case "ble":
        binaryLine = instructionInIType(
                "000111",
                instructionParts.get(1),
                instructionParts.get(2),
                instructionParts.get(3));
        break;
      case "bne":
        binaryLine = instructionInIType(
                "000100",
                instructionParts.get(1),
                instructionParts.get(2),
                instructionParts.get(3));
        break;
      case "jmp":
        binaryLine = instructionInJType(
                "000010",
                instructionParts.get(1));
        break;
      case "lw":
        binaryLine = instructionLoadStore(
                "100011",
                instructionParts.get(1),
                instructionParts.get(2));
        break;
      case "mul":
        binaryLine = instructionInRType(
                "000000",
                "011000",
                instructionParts.get(2),
                instructionParts.get(3),
                instructionParts.get(1));
        break;
      case "nop":
        binaryLine = "00000000000000000000000000000000";
        break;
      case "sub":
        binaryLine = instructionInRType(
                "000000",
                "100010",
                instructionParts.get(2),
                instructionParts.get(3),
                instructionParts.get(1));
        break;
      case "sw":
        binaryLine = instructionLoadStore(
                "101011",
                instructionParts.get(1),
                instructionParts.get(2));
        break;
      case "li":
        binaryLine = instructionInIType(
                "001000",
                "r0",
                instructionParts.get(1),
                instructionParts.get(2));
        break;
    }

    return binaryLine + "    ;" + line.trim();
  }

  private static List<String> getInstructionParts(String instruction, HashMap<String, Integer> labels){
    List<String> instructionParts;
    int index;
    instruction = instruction.trim();
    if(instruction.contains(";")){
      index =  instruction.indexOf(';');
      instruction = instruction.substring(0,index);
    }
    instruction = instruction.replaceFirst(" ", ",");
    instructionParts = Arrays.asList(instruction.split(","));

    for(int i = 0; i < instructionParts.size(); i++){
      String element;
      element = instructionParts.get(i);
      element = element.trim();
      element = element.toLowerCase();
      instructionParts.set(i, element);
    }

    String op = instructionParts.get(0);
    if("ble".equals(op) || "beq".equals(op) || "bne".equals(op)){
      String label = instructionParts.get(3);
      if(!label.matches("\\d+")) {
        Integer numericLabel = labels.get(label);
        instructionParts.set(3, numericLabel.toString());
      }
    }
    else if ("jmp".equals(op)){
      String label = instructionParts.get(1);
      if(!label.matches("\\d+")) {
        Integer numericLabel = labels.get(label);
        instructionParts.set(1, numericLabel.toString());
      }
    }

    return instructionParts;
  }

  public static void writeToFile(String filename, List<String> lines){
    File file = new File(filename);
    FileWriter output = null;
    BufferedWriter bufferedWriter = null;

    try {
      output = new FileWriter(file);
      bufferedWriter = new BufferedWriter(output);
      for(String line : lines){
        bufferedWriter.write(line);
        bufferedWriter.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (output != null) {
        try {
          bufferedWriter.close();
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static List<String> readFromFile(String filename){
    List<String> lines;
    FileReader file = null;
    try {
      file = new FileReader(filename);
      BufferedReader reader = new BufferedReader(file);
      String line = "";
      lines = new ArrayList<>();
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found");
    } catch (IOException e){
      throw new RuntimeException("IO Error occured");
    } finally {
      if (file != null) {
        try {
          file.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return lines;
  }

}
