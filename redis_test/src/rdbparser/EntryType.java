package rdbparser;

import rdbparser.entry.*;


public enum EntryType {
  EOF,
  
  SELECT_DB,

  KEY_VALUE_PAIR,
  
  RESIZE_DB,

  AUX_FIELD
}
