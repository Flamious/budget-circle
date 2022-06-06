namespace BudgetCircleApi.Controllers
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using Microsoft.AspNetCore.Authorization;
    using Microsoft.AspNetCore.Mvc;
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Security.Claims;
    using System.Threading.Tasks;

    [ApiController]
    [Route("[controller]")]
    public class OperationController : ControllerBase
    {
        private readonly IOperationsService _operationsServices;

        public OperationController(IOperationsService operationsServices)
        {
            _operationsServices = operationsServices;
        }


        [HttpGet]
        [Route("chart")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> GetChartOperations([FromQuery] ChartOperationRequest request)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var result = await Task.Run(() => _operationsServices.GetChartOperations(userId, request));

                return Ok(result);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpGet]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> GetAllOperations([FromQuery] OperationRequest request)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var result = await Task.Run(() => _operationsServices.GetOperations(userId, request));

                return Ok(result);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpGet]
        [Route("sum")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> GetOperationSums([FromQuery] OperationRequestSum request)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var result = await Task.Run(() => _operationsServices.GetOperationSum(userId, request));

                return Ok(result);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPut]
        [Route("add_many")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> AddOperation([FromBody] ManyOperationsBody body)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState.Values.SelectMany(e => e.Errors.Select(er => er.ErrorMessage)));
            }

            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _operationsServices.AddManyOperation(userId, body.models);

                return Ok();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPut]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> AddOperation([FromQuery] OperationModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState.Values.SelectMany(e => e.Errors.Select(er => er.ErrorMessage)));
            }

            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var response = await _operationsServices.AddOperation(userId, model);

                if (response is ErrorResponse)
                {
                    return BadRequest(response);
                }

                return Ok(response);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpDelete]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> RemoveAllOperation()
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _operationsServices.RemoveAllOperations(userId);

                return NoContent();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpDelete]
        [Route("{id:int}")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> RemoveOperation([FromRoute] int id)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _operationsServices.RemoveOperation(userId, id);

                return NoContent();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }


        [HttpPost]
        [Route("{id:int}")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> UpdateOperation([FromRoute] int id, [FromQuery] OperationModel model)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _operationsServices.UpdateOperation(userId, id, model);

                return Ok();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }
    }
}
