package app.tmsbackend.service

import app.tmsbackend.model.Party
import app.tmsbackend.model.PartyDTO
import app.tmsbackend.repository.PartyRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PartyService(private val partyRepository: PartyRepository) {

    fun createParty(party: Party): Party {
        val partyDTO = PartyDTO(
            id = party.id ?: UUID.randomUUID().toString(),
            name = party.name,
            pointOfContact = party.pointOfContact,
            contactNumber = party.contactNumber,
            email = party.email,
            addressLine1 = party.addressLine1,
            addressLine2 = party.addressLine2,
            state = party.state,
            district = party.district,
            taluka = party.taluka,
            city = party.city,
            pinCode = party.pinCode,
            createdAt = party.createdAt ?: Instant.now().epochSecond
        )
        val createdPartyDTO = partyRepository.createParty(partyDTO)
        return createdPartyDTO.toParty()
    }

    fun updateParty(party: Party): Party {
        val partyDTO = PartyDTO(
            id = party.id ?: throw IllegalArgumentException("Party ID cannot be null"),
            name = party.name,
            pointOfContact = party.pointOfContact,
            contactNumber = party.contactNumber,
            email = party.email,
            addressLine1 = party.addressLine1,
            addressLine2 = party.addressLine2,
            state = party.state,
            district = party.district,
            taluka = party.taluka,
            city = party.city,
            pinCode = party.pinCode,
            createdAt = party.createdAt ?: Instant.now().epochSecond
        )
        val updatedPartyDTO = partyRepository.updateParty(partyDTO)
        return updatedPartyDTO.toParty()
    }

    fun getParty(id: String): Party {
        val partyDTO = partyRepository.getParty(id) ?: throw IllegalArgumentException("Party not found with ID: $id")
        return partyDTO.toParty()
    }

    fun listParties(search: String, page: Int, size: Int): List<Party> {
        val partyDTOs = partyRepository.listParties(search, page, size)
        return partyDTOs.map { it.toParty() }
    }

    fun listAllParties(): List<Party> {
        val partyDTOs = partyRepository.listAllParties()
        return partyDTOs.map { it.toParty() }
    }
}
