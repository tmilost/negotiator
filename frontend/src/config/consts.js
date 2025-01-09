const dateFormat = 'LL - LT'

const ROLES = {
  RESEARCHER: 'ROLE_RESEARCHER',
  REPRESENTATIVE: 'ROLE_REPRESENTATIVE',
  ADMINISTRATOR: 'ROLE_ADMIN',
}

const MESSAGE_STATUS = {
  SENT: 'CREATED',
  READ: 'READ',
}

const POST_TYPE = {
  PUBLIC: 'PUBLIC',
  PRIVATE: 'PRIVATE',
}

const NEGOTIATION_STATUS = {
  SUBMITTED: 'SUBMITTED',
  IN_PROGRESS: 'IN_PROGRESS',
  ABANDONED: 'ABANDONED',
}

export { MESSAGE_STATUS, ROLES, POST_TYPE, NEGOTIATION_STATUS, dateFormat }
